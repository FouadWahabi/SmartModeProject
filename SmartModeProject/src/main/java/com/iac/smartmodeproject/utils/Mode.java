package com.iac.smartmodeproject.utils;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.iac.smartmodeproject.listeners.IModeListener;

public abstract class Mode {

	private String TAG = "Mode";
	protected IModeListener modeListener;
	public boolean isActive = true;
	public boolean isApllied = false;
	protected String smsMsg;
	protected Context context;
	// recent mode values
	protected int[] recentVomlumeValues = { 100, 100, 100, 100 };
	protected int ringerMode;
	protected boolean smsOption = true;

	public Mode() {

	}

	public Mode(Context contex) {
		this.context = contex;
	}

	public abstract void init();

	public abstract void apply();

	public abstract void dispatch();

	public void setOnCheckModeListener(IModeListener modeListener) {
		this.modeListener = modeListener;
		checkForAppliance();
	}

	public void removeListenerCallback() {
		this.modeListener = null;
	}

	public abstract void checkForAppliance();

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setEnabled(boolean isEnabled) {
		Log.i("SetEnabled", this.toString());
		this.isActive = isEnabled;
	}

	private void saveCurrentPhoneState() {
		Log.i(TAG, "saveRecentModeValues : save recent mode values");
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		recentVomlumeValues[0] = audioManager
				.getStreamVolume(AudioManager.STREAM_SYSTEM);
		recentVomlumeValues[1] = audioManager
				.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		recentVomlumeValues[2] = audioManager
				.getStreamVolume(AudioManager.STREAM_RING);
		recentVomlumeValues[3] = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		ringerMode = audioManager.getRingerMode();
	}

	public abstract void onDestroy();

	public abstract String toString();

	public void enableSmsOption() {
		smsOption = true;
	}

	public void disableSmsOption() {
		smsOption = false;
	}

	public void setSmsMsg(String smsMsg) {
		this.smsMsg = smsMsg;
	}

}
