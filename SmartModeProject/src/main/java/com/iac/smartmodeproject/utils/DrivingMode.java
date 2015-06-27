package com.iac.smartmodeproject.utils;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.listeners.IActivityRencognitionListener;
import com.iac.smartmodeproject.listeners.ILocationTrackingListener;
import com.iac.smartmodeproject.managers.ActivityRecognitionManager;
import com.iac.smartmodeproject.managers.LocationTrackerManager;
import com.iac.smartmodeproject.managers.NotificationManager;
import com.iac.smartmodeproject.managers.SettingManager;
import com.iac.smartmodeproject.ui.widgets.AppTextView;

public class DrivingMode extends Mode implements IActivityRencognitionListener,
		ILocationTrackingListener, OnInitListener {

	private String TAG = "DrivingMode";
	private ActivityRecognitionManager activityRecognitionManager;
	private TelephonyManager telephony;
	private PhoneStateListener phoneListener;
	private AudioManager audioManager;
	private boolean recentCall = false;
	private String lastCallNumber;
	private SmsManager smsManager = SmsManager.getDefault();
	private LocationTrackerManager locationTrackerManager;
	private AppTextView speedCounterTv;
	private boolean ttsEnabled = true;
	private String ttsApplyMode = "Driving Mode is applied, your SMS will be talked up";
	private TextToSpeech textToSpeech;
	private BroadcastReceiver smsReceiver;
	private boolean isSystemMute = false;
	private NotificationManager notificationManager;
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void init() {
		smsMsg = context.getString(R.string.driving_mode_sms);

		if (notificationManager == null)
			notificationManager = NotificationManager.getINSATANCE(context);

		activityRecognitionManager = ActivityRecognitionManager
				.getINSTANCE(context);
		locationTrackerManager = new LocationTrackerManager(context);
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		textToSpeech = new TextToSpeech(context, this);

		// is this mode is enabled
		isActive = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.ENABLED_DISABLED_DRIVING_MODE, true);

		smsOption = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.DRIVING_MODE_SMS_OPTION, true);
		ttsEnabled = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.DRIVING_MODE_TTS_OPTION, true);

		smsMsg = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_MSG, smsMsg);

		if (!isActive) {
			setEnabled(isActive);
		}
	}

	@Override
	public void apply() {
		if (isActive && !isApllied) {
			isApllied = true;
			Toast.makeText(context, "Apply : apllying driving mode",
					Toast.LENGTH_LONG).show();

			notificationManager
					.createNotification(NotificationManager.DRIVING_MODE_NOTIFICATION);

			dispatchMuteMode(audioManager);

			textToSpeech.speak(ttsApplyMode, TextToSpeech.QUEUE_ADD, null);
			// make system mute
			makeSystemMute(audioManager);

			if (telephony == null)
				telephony = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
			phoneListener = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					if (state == TelephonyManager.CALL_STATE_RINGING) {
						if (!isSystemMute)
							makeSystemMute(audioManager);
						recentCall = true;
						lastCallNumber = incomingNumber;
					}
					if (state == TelephonyManager.CALL_STATE_IDLE) {
						if (recentCall) {
							recentCall = false;
							if (smsOption) {
								smsManager.sendTextMessage(lastCallNumber,
										null, smsMsg, null, null);
							}
						}
					}
				}
			};
			if (smsOption)
				telephony.listen(phoneListener,
						PhoneStateListener.LISTEN_CALL_STATE);

			smsReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getAction().equals(SMS_RECEIVED)) {
						if (ttsEnabled) {
							Bundle extras = intent.getExtras();
							Object[] smsExtra = (Object[]) extras.get("pdus");
							for (int i = 0; i < smsExtra.length; i++) {
								String messages = "";
								SmsMessage sms = SmsMessage
										.createFromPdu((byte[]) smsExtra[i]);
								String body = sms.getMessageBody().toString();
								String address = sms.getOriginatingAddress();

								messages += "SMS from " + address + " :\n";
								messages += body + "\n";
								// speak sms
								dispatchMuteMode(audioManager);
								textToSpeech.speak(messages,
										TextToSpeech.QUEUE_ADD, null);
								makeSystemMute(audioManager);
							}
						}
					}
				}
			};
			context.registerReceiver(smsReceiver,
					new IntentFilter(SMS_RECEIVED));

			// save log datas
			SettingManager.getINSTANCE().addLog(SettingManager.DRIVING_MODE,
					null, System.currentTimeMillis());
		}
	}

	@Override
	public void checkForAppliance() {
		if (isActive) {
			// activity recognition
			activityRecognitionManager.addActivityRecognitionListener(this);
			activityRecognitionManager.startActivityRecognitionTracking();

			// location and speed tracking
			locationTrackerManager.addLocationTrackerListener(this);
			locationTrackerManager.setLocationUpdates(true);
			locationTrackerManager.setLocationTrackingInterval(5000);
			locationTrackerManager.startLocationTracking();

			// modeListener.onAbleForAppliance();
		}
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onSpeedChanged(double speed) {
		speed = (double) Math.round(speed * 100) / 100;
		if (speedCounterTv != null) {
			speedCounterTv.setText(speed + "");
		}
	}

	@Override
	public void onActivityStateChanged(ActivityRecognitionResult result) {

		// when user detected at vehicle the mode is able for appliance
		if (result.getMostProbableActivity().getType() == DetectedActivity.IN_VEHICLE) {
			modeListener.onAbleForAppliance();
		} else {
			modeListener.onDispatchMode();
		}

	}

	private void makeSystemMute(AudioManager audioManager) {
		// TODO Auto-generated method stub
		isSystemMute = true;
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
		isSystemMute = false;
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
		if (isApllied) {
			isApllied = false;
			AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			dispatchMuteMode(audioManager);
		}
	}

	@Override
	public void onDestroy() {
		if (modeListener != null)
			modeListener.onDispatchMode();
		if (activityRecognitionManager != null) {
			activityRecognitionManager.stopActivityRecognitionTracking();
		}
		if (locationTrackerManager != null) {
			locationTrackerManager.stopLocationUpdates();
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "DrivingMode";
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		super.setEnabled(isEnabled);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.ENABLED_DISABLED_DRIVING_MODE, isEnabled);
		if (!isEnabled) {
			onDestroy();
		} else {
			checkForAppliance();
		}
	}

	public void setSpeedCounterTv(AppTextView tv) {
		this.speedCounterTv = tv;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = textToSpeech.setLanguage(Locale.UK);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e(TAG, "This Language is not supported");
			}
		} else {
			Log.i(TAG, "Initilization Failed!");
		}
	}

	public void setEnabledTtsOption(boolean enabled) {
		if (!enabled)
			textToSpeech.stop();
		this.ttsEnabled = enabled;
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.DRIVING_MODE_TTS_OPTION, enabled);
	}

	@Override
	public void enableSmsOption() {
		// TODO Auto-generated method stub
		super.enableSmsOption();
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.DRIVING_MODE_SMS_OPTION, true);
	}

	@Override
	public void disableSmsOption() {
		// TODO Auto-generated method stub
		super.disableSmsOption();
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.DRIVING_MODE_SMS_OPTION, false);
	}

	@Override
	public void setSmsMsg(String smsMsg) {
		// TODO Auto-generated method stub
		super.setSmsMsg(smsMsg);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.DRIVING_MODE_SMS_MSG, smsMsg);
	}
}
