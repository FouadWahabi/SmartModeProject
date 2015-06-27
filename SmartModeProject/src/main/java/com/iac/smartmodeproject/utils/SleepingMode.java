package com.iac.smartmodeproject.utils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.listeners.ILightSensorListener;
import com.iac.smartmodeproject.listeners.INoiseLevelListener;
import com.iac.smartmodeproject.managers.LightSensorManager;
import com.iac.smartmodeproject.managers.NotificationManager;
import com.iac.smartmodeproject.managers.SettingManager;
import com.iac.smartmodeproject.managers.VoiceCaptureManager;
import com.iac.smartmodeproject.receivers.SleepingTimeReceiver;

public class SleepingMode extends Mode implements ILightSensorListener {

	private String TAG = "SleepingMode";
	public List<String> urgentPhoneNumbers = null;

	private TelephonyManager telephony;
	private PhoneStateListener phoneListener;

	private LightSensorManager lightSensorManager;
	private VoiceCaptureManager audioCaptureManager = VoiceCaptureManager
			.getInsatnce();
	private NotificationManager notificationManager;

	// setting time fields
	private Calendar c = Calendar.getInstance();

	private long sleepingTime = 0;
	private boolean recentCall = false;
	private String lastCallNumber;
	private SmsManager smsManager = SmsManager.getDefault();

	public Runnable r;
	private PendingIntent alarmIntent;
	private Intent sleep_time_rec_intent;
	private AlarmManager alarmManger;

	// responsible for initiating sleeping mode and loading saved settings
	@Override
	public void init() {

		c.set(Calendar.HOUR_OF_DAY, 21);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		sleepingTime = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.SLEEPING_TIME, c.getTimeInMillis());

		if (notificationManager == null)
			notificationManager = NotificationManager.getINSATANCE(context);

		lightSensorManager = LightSensorManager.getINSTANCE();
		lightSensorManager.init(context);

		isActive = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.ENABLED_DISABLED_SLEEPING_MODE, true);
		if (urgentPhoneNumbers == null) {
			urgentPhoneNumbers = new LinkedList<String>();
			try {
				JSONArray json_array = new JSONArray(
						SettingManager.settingPreferences.getString(
								SettingManager.URGENT_CALLS, "[]"));
				if (json_array != null) {
					for (int i = 0; i < json_array.length(); i++) {
						urgentPhoneNumbers.add(json_array.getString(i));
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// is this mode is enabled
		isActive = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.ENABLED_DISABLED_SLEEPING_MODE, true);

		smsOption = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.SLEEPING_MODE_SMS_OPTION, true);

		smsMsg = context.getString(R.string.sleeping_mode_sms);

		smsMsg = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_MSG, smsMsg);

		if (!isActive) {
			setEnabled(isActive);
		}
	}

	// responsible for applying sleeping mode
	@Override
	public void apply() {
		// TODO à reviser
		if (this.isActive && !isApllied) {
			isApllied = true;

			notificationManager
					.createNotification(NotificationManager.SLEEPING_MODE_NOTIFICATION);

			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			// make system mute
			makeSystemMute(audioManager);
			if (telephony == null)
				telephony = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
			phoneListener = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					Log.i(TAG, "Call received");
					if (state == TelephonyManager.CALL_STATE_RINGING) {
						Log.i(TAG,
								"onCallStateChanged : icoming urgent call from "
										+ incomingNumber);
						if (urgentPhoneNumbers != null) {
							if (contains(urgentPhoneNumbers, incomingNumber)) {
								if (audioManager
										.getStreamVolume(AudioManager.STREAM_RING) != audioManager
										.getStreamMaxVolume(AudioManager.STREAM_RING)) {
									audioManager
											.setStreamVolume(
													AudioManager.STREAM_RING,
													audioManager
															.getStreamMaxVolume(AudioManager.STREAM_RING),
													AudioManager.FLAG_PLAY_SOUND);
								}
							} else {

								recentCall = true;
								lastCallNumber = incomingNumber;
								if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
									audioManager
											.setRingerMode(AudioManager.RINGER_MODE_SILENT);
								}
							}
						}
					} else {
						if (state == TelephonyManager.CALL_STATE_IDLE) {
							if (recentCall) {
								recentCall = false;
								if (smsOption) {
									smsManager.sendTextMessage(lastCallNumber,
											null, smsMsg, null, null);
								}
							}
							makeSystemMute(audioManager);
						}
					}
				}
			};
			telephony.listen(phoneListener,
					PhoneStateListener.LISTEN_CALL_STATE);
			// save log datas
			SettingManager.getINSTANCE().addLog(SettingManager.SLEEPING_MODE,
					null, System.currentTimeMillis());
		}
	}

	private void makeSystemMute(AudioManager audioManager) {
		// TODO Auto-generated method stub
		Log.i(TAG, "makeSystemMute : make device silent");
		if (audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) != 0)
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0)
			audioManager.setStreamVolume(AudioManager.STREAM_RING, 0,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0)
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}

	private void dispatchMuteMode(AudioManager audioManager) {
		Log.i(TAG, "pispatchMuteMode : add listener to the light sensor");
		if (audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) == 0)
			audioManager
					.setStreamVolume(AudioManager.STREAM_SYSTEM, audioManager
							.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
							AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0)
			audioManager.setStreamVolume(AudioManager.STREAM_RING,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) == 0)
			audioManager
					.setStreamVolume(
							AudioManager.STREAM_NOTIFICATION,
							audioManager
									.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),
							AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}

	@Override
	public void dispatch() {
		// TODO Auto-generated method stub
		if (isApllied == true) {
			isApllied = false;
			AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			dispatchMuteMode(audioManager);
			if ((telephony != null) && (phoneListener != null)) {
				telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
			}
		}
	}

	@Override
	public void checkForAppliance() {
		// TODO Auto-generated method stub
		if (this.isActive) {
			Log.i(TAG,
					"checkForAppliance : check for mode appliance includes deeling with light sensor and mic");
			// initialize datas
			alarmManger = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			// init runnable which will be called once SleepTimeReceiver
			// have received intent it have to be initiated before setting
			// AlarmManager
			r = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.i(TAG, "run: called once alarmManager declanched");
					Log.i(TAG, "run : " + Thread.currentThread().getName());
					if (lightSensorManager.isLightSensorExist()) {
						Log.i("SleepingMode", "Light sensor available");
						lightSensorManager
								.registreLightSensorListener(SleepingMode.this);
						lightSensorManager.startTamperingLight();
					} else {
						Log.i("SleepingMode", "Light sensor not available");
					}
				}
			};

			// setting AlarmManager
			sleep_time_rec_intent = new Intent(context,
					SleepingTimeReceiver.class);
			alarmIntent = PendingIntent.getBroadcast(context, 0,
					sleep_time_rec_intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarmManger.setRepeating(AlarmManager.RTC_WAKEUP, sleepingTime,
					AlarmManager.INTERVAL_DAY, alarmIntent);
		}

	}

	@Override
	public void onSensorChanged(float lightLevel) {
		Log.i(TAG, "onSensorChanged : " + lightLevel);
		if (lightLevel < 30) {
			// capture audio only if mode is not applied
			if (!isApllied) {
				audioCaptureManager.init(context);
				audioCaptureManager.startRecording();
				audioCaptureManager.setNoiseLevelListener(1000,
						new INoiseLevelListener() {

							@Override
							public void noiseLevelReceived(int noise) {
								// TODO Auto-generated method stub
								if (noise == 0) {

									modeListener.onAbleForAppliance();

									Log.i(TAG,
											"noiseLevelReceived : apply sleeping mode");

								}
							}
						});
			}
		} else {
			if (isApllied) {
				modeListener.onDispatchMode();
				lightSensorManager.stopTampering();
			}
		}
	}

	public void updateAlarm() {
		Log.i(TAG, "updateAlarm : update alarm time");
		if (modeListener != null) {
			modeListener.onDispatchMode();
		}

		if (isActive) {
			if (lightSensorManager != null) {
				lightSensorManager.stopTampering();
			}
			if (alarmManger != null && alarmIntent != null) {
				alarmManger.cancel(alarmIntent);
				alarmManger.setRepeating(AlarmManager.RTC_WAKEUP, sleepingTime,
						AlarmManager.INTERVAL_DAY, alarmIntent);
			}
		}
	}

	public void addUrgentPhoneNumber(String phoneNumber) {
		Log.i(TAG, "addEurgentPhoneNumber : add urgent phone number "
				+ phoneNumber);
		if (urgentPhoneNumbers != null) {
			if (!urgentPhoneNumbers.contains(phoneNumber))
				urgentPhoneNumbers.add(phoneNumber);
			else
				Toast.makeText(context, "This phone number exist",
						Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy : destroy mode");
		if (modeListener != null)
			modeListener.onDispatchMode();
		if (alarmManger != null && alarmIntent != null) {
			alarmIntent.cancel();
			alarmManger.cancel(alarmIntent);
		}
		if (lightSensorManager.isLightSensorExist()) {
			lightSensorManager.stopTampering();
			lightSensorManager.unregisterLightSensorListener(this);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SleepingMode";
	}

	// getters setters
	public long getSleepingTime() {
		return sleepingTime;
	}

	public void setSleepingTime(long time) {
		this.sleepingTime = time;
		Log.i("NominalTime changed", time + "");
	}

	public void setUrgentPhoneNumbers(List<String> urgentPhoneCall) {
		this.urgentPhoneNumbers = urgentPhoneCall;
	}

	public List<String> getUrgentPhoneNumbers() {
		return this.urgentPhoneNumbers;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		// TODO Auto-generated method stub
		super.setEnabled(isEnabled);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.ENABLED_DISABLED_SLEEPING_MODE, isEnabled);
		if (!isEnabled) {
			onDestroy();
		} else {
			checkForAppliance();
		}
	}

	@Override
	public void enableSmsOption() {
		// TODO Auto-generated method stub
		super.enableSmsOption();
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.SLEEPING_MODE_SMS_OPTION, true);
	}

	@Override
	public void disableSmsOption() {
		// TODO Auto-generated method stub
		super.disableSmsOption();
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.SLEEPING_MODE_SMS_OPTION, false);
	}

	@Override
	public void setSmsMsg(String smsMsg) {
		// TODO Auto-generated method stub
		super.setSmsMsg(smsMsg);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.SLEEPING_MODE_SMS_MSG, smsMsg);
	}

	private boolean contains(List<String> phoneNumbers, String phoneNumber) {
		for (String phone : phoneNumbers) {
			if (PhoneNumberUtils.compare(phone, phoneNumber)) {
				return true;
			}
		}
		return false;
	}
}
