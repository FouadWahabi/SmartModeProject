package com.iac.smartmodeproject.utils;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.managers.NotificationManager;
import com.iac.smartmodeproject.managers.SettingManager;

public class EventMode extends Mode {

	private String TAG = "EventMode";
	private Handler eventReminderHandler = new Handler();
	private Runnable r;
	private BroadcastReceiver eventReminder;
	private TelephonyManager telephony;
	private PhoneStateListener phoneListener;
	private boolean recentCall = false;
	private String lastCallNumber;
	private SmsManager smsManager = SmsManager.getDefault();
	private NotificationManager notificationManager;
	private String actualEvent;

	// options

	@Override
	public void init() {
		smsMsg = context.getString(R.string.event_mode_sms);

		if (notificationManager == null)
			notificationManager = NotificationManager.getINSATANCE(context);

		// is this mode is enabled
		isActive = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.ENABLED_DISABLED_EVENT_MODE, true);

		smsOption = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_OPTION, true);

		smsMsg = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_MSG, smsMsg);
		if (!isActive) {
			setEnabled(isActive);
		}
	}

	@Override
	public void apply() {

		Log.i(TAG, "Apply : apllying event mode");
		if (isActive && !isApllied) {
			isApllied = true;

			notificationManager
					.createNotification(NotificationManager.EVENT_MODE_NOTIFICATION);

			AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			// make system mute
			makeSystemMute(audioManager);

			if (telephony == null)
				telephony = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
			phoneListener = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					if (state == TelephonyManager.CALL_STATE_RINGING) {
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

			// save log datas
			SettingManager.getINSTANCE().addLog(SettingManager.EVENT_MODE,
					actualEvent, System.currentTimeMillis());
		}
	}

	@Override
	public void checkForAppliance() {
		// TODO Auto-generated method stub
		if (isActive) {

			r = new Runnable() {
				@Override
				public void run() {
					modeListener.onDispatchMode();
				}
			};

			eventReminder = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					ContentResolver cr = context.getContentResolver();
					Calendar cal = Calendar.getInstance(TimeZone
							.getTimeZone("GMT"));
					long time = cal.getTimeInMillis();
					Cursor c = null;
					try {
						if ((c = cr.query(Events.CONTENT_URI, new String[] {
								Events._ID, CalendarContract.Events.TITLE,
								Events.DTSTART, Events.DTEND }, "(("
								+ Events.DTSTART + " <= " + time + ") AND ("
								+ Events.DTEND + " >= " + time + ") AND ( "
								+ Events.AVAILABILITY + " = "
								+ Events.AVAILABILITY_BUSY + " ))",
								new String[] {}, null)).getCount() > 0) {
							c.moveToFirst();

							actualEvent = c.getString(1);
							// mode applicable
							modeListener.onAbleForAppliance();

							Calendar cl = Calendar.getInstance();
							cl.setTimeInMillis(c.getLong(3));
							while (c.moveToNext()) {
								if (c.getLong(3) > cl.getTimeInMillis()) {
									cl.setTimeInMillis(c.getLong(3));
								}
							}
							cl.setTimeZone(TimeZone.getDefault());
							eventReminderHandler.removeCallbacks(r);
							eventReminderHandler.postDelayed(
									r,
									cl.getTimeInMillis()
											- System.currentTimeMillis());
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						try {
							if (c != null && !c.isClosed())
								c.close();
						} catch (Exception ex) {
						}
					}
				}
			};
			IntentFilter filter = new IntentFilter(
					CalendarContract.ACTION_EVENT_REMINDER);
			filter.addDataScheme("content");
			context.registerReceiver(eventReminder, filter);
		}
	}

	@Override
	public void dispatch() {
		// TODO Auto-generated method stub
		Log.i(TAG, "Dispatch : dispatching event mode");
		if (isApllied) {
			isApllied = false;
			AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			dispatchMuteMode(audioManager);
			if (eventReminderHandler != null && r != null) {
				eventReminderHandler.removeCallbacks(r);
			}
			Toast.makeText(context, "Dispatch : dispatching event mode",
					Toast.LENGTH_LONG).show();
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
	public void onDestroy() {

		if (modeListener != null)
			modeListener.onDispatchMode();
		if (eventReminderHandler != null && r != null) {
			eventReminderHandler.removeCallbacks(r);
		}
		if (eventReminder != null)
			context.unregisterReceiver(eventReminder);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "EventMode";
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		// TODO Auto-generated method stub
		super.setEnabled(isEnabled);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.ENABLED_DISABLED_EVENT_MODE, isEnabled);
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
				SettingManager.EVENT_MODE_SMS_OPTION, true);
	}

	@Override
	public void disableSmsOption() {
		// TODO Auto-generated method stub
		super.disableSmsOption();
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.EVENT_MODE_SMS_OPTION, false);
	}

	@Override
	public void setSmsMsg(String smsMsg) {
		// TODO Auto-generated method stub
		super.setSmsMsg(smsMsg);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.EVENT_MODE_SMS_MSG, smsMsg);
	}
}
