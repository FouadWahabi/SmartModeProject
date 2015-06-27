package com.iac.smartmodeproject.utils;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.helpers.PlaceDBHelper;
import com.iac.smartmodeproject.helpers.SQLiteCursorLoader;
import com.iac.smartmodeproject.listeners.ILocationTrackingListener;
import com.iac.smartmodeproject.managers.LocationTrackerManager;
import com.iac.smartmodeproject.managers.NotificationManager;
import com.iac.smartmodeproject.managers.SettingManager;

public class PlaceMode extends Mode implements ILocationTrackingListener {

	private String TAG = "PlaceMode";
	private TelephonyManager telephony;
	private PhoneStateListener phoneListener;
	private boolean recentCall = false;
	private String lastCallNumber;
	private SmsManager smsManager = SmsManager.getDefault();
	private LocationTrackerManager locationTrackerManager;
	private NotificationManager notificationManager;
	private int currentLocation = -1;

	@Override
	public void init() {
		smsMsg = context.getString(R.string.place_mode_sms);

		locationTrackerManager = new LocationTrackerManager(context);
		if (notificationManager == null)
			notificationManager = NotificationManager.getINSATANCE(context);
		locationTrackerManager.setLocationUpdates(true);

		// load if mode is enabled
		isActive = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.ENABLED_DISABLED_PLACE_MODE, true);

		smsOption = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.PLACE_MODE_SMS_OPTION, true);

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
			notificationManager
					.createNotification(NotificationManager.PLACE_MODE_NOTIFICATION);

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
			SettingManager.getINSTANCE().addLog(SettingManager.PLACE_MODE,
					placeNameFromId(currentLocation),
					System.currentTimeMillis());
		}
	}

	private void makeSystemMute(AudioManager audioManager) {

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
	public void checkForAppliance() {
		// TODO Auto-generated method stub
		if (isActive) {
			locationTrackerManager.addLocationTrackerListener(this);
			locationTrackerManager.startLocationTracking();

		}
	}

	@Override
	public void onLocationChanged(Location location) {

		// check if the place exist in database
		if (location.getAccuracy() < 50) {
			currentLocation = search(location);
			if (currentLocation > -1) {
				modeListener.onAbleForAppliance();
			} else {
				modeListener.onDispatchMode();
			}
		}
	}

	@Override
	public void onSpeedChanged(double speed) {

		// test current location
		/*
		 * Toast.makeText(context, "Speed changed : " + speed,
		 * Toast.LENGTH_SHORT) .show();
		 */
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
		if (locationTrackerManager != null) {
			locationTrackerManager.stopLocationUpdates();
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "PlaceMode";
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		// TODO Auto-generated method stub
		super.setEnabled(isEnabled);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.ENABLED_DISABLED_PLACE_MODE, isEnabled);
		if (!isEnabled) {
			onDestroy();
		} else {
			checkForAppliance();
		}
	}

	private String placeNameFromId(int id) {
		PlaceDBHelper db = new PlaceDBHelper(context);
		String rawQuery = "SELECT * FROM " + PlaceDBHelper.TABLE_NAME
				+ " WHERE " + PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_ID]
				+ " = " + id;
		SQLiteCursorLoader mLoader = new SQLiteCursorLoader(context, db,
				rawQuery, null);
		Cursor cursor = mLoader.buildCursor();
		cursor.moveToFirst();
		String place_name = cursor.getString(PlaceDBHelper.COLUMN_PLACE_LABEL);
		cursor.close();
		db.close();
		return place_name;
	}

	// search for current place if it's saved and returns the id of the place or
	// -1 if not exist

	private int search(Location location) {
		int id = -1;
		PlaceDBHelper db = new PlaceDBHelper(context);
		String rawQuery = "SELECT * FROM " + PlaceDBHelper.TABLE_NAME;
		SQLiteCursorLoader mLoader = new SQLiteCursorLoader(context, db,
				rawQuery, null);
		Cursor cursor = mLoader.buildCursor();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Location dest = new Location("");
				dest.setLatitude(cursor
						.getDouble(PlaceDBHelper.COLUMN_PLACE_LATT));
				dest.setLongitude(cursor
						.getDouble(PlaceDBHelper.COLUMN_PLACE_LANG));
				if (location.distanceTo(dest) <= cursor
						.getInt(PlaceDBHelper.COLUMN_PLACE_RADIUS)) {
					id = cursor.getInt(PlaceDBHelper.COLUMN_ID);
					cursor.close();
					return id;
				}
			}
		}
		cursor.close();
		db.close();
		return id;
	}

	@Override
	public void enableSmsOption() {
		// TODO Auto-generated method stub
		super.enableSmsOption();
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.PLACE_MODE_SMS_OPTION, true);
	}

	@Override
	public void disableSmsOption() {
		// TODO Auto-generated method stub
		super.disableSmsOption();
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.PLACE_MODE_SMS_OPTION, false);
	}

	@Override
	public void setSmsMsg(String smsMsg) {
		// TODO Auto-generated method stub
		super.setSmsMsg(smsMsg);
		SettingManager.getINSTANCE().modifySetting(
				SettingManager.PLACE_MODE_SMS_MSG, smsMsg);
	}
}
