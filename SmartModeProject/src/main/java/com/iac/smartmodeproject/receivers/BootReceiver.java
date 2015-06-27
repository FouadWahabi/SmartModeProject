package com.iac.smartmodeproject.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iac.smartmodeproject.services.MainService;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			context.startService(new Intent(context, MainService.class));
		}
	}
}
