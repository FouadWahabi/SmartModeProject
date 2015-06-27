package com.iac.smartmodeproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.iac.smartmodeproject.managers.ModeManager;

public class MainService extends Service {

	private String TAG = "MainService";

	private ModeManager modeManager = ModeManager.getINSTANCE();
	public static boolean serviceStarted = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand : Starting service");
		MainService.serviceStarted = true;

		// initiate listeners
		modeManager.initListeners(getApplicationContext());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy : destroying service");
		serviceStarted = false;
		modeManager.unregisterListeners();
		super.onDestroy();
	}

}
