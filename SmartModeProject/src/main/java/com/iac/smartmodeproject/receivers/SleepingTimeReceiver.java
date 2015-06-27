package com.iac.smartmodeproject.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iac.smartmodeproject.managers.ModeManager;
import com.iac.smartmodeproject.services.MainService;
import com.iac.smartmodeproject.utils.SleepingMode;

public class SleepingTimeReceiver extends BroadcastReceiver {

	private String TAG = "SleepModeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onReceive : called when an AlarmManger is declanched");

		if (!MainService.serviceStarted
				|| ((SleepingMode) ModeManager.getINSTANCE().sleepingMode).r == null) {
			Intent service_intent = new Intent(context, MainService.class);
			context.startService(service_intent);
			return;
		}
		((SleepingMode) ModeManager.getINSTANCE().sleepingMode).r.run();
	}
}
