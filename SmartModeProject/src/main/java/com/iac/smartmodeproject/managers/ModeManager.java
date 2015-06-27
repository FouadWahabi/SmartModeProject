package com.iac.smartmodeproject.managers;

import android.content.Context;
import android.util.Log;

import com.iac.smartmodeproject.listeners.IModeListener;
import com.iac.smartmodeproject.utils.DrivingMode;
import com.iac.smartmodeproject.utils.EventMode;
import com.iac.smartmodeproject.utils.Mode;
import com.iac.smartmodeproject.utils.PlaceMode;
import com.iac.smartmodeproject.utils.SleepingMode;

public class ModeManager {

	private String TAG = "ModeManager";
	private static ModeManager INSTANCE = new ModeManager();
	private Context context;

	public Mode sleepingMode = new SleepingMode();
	public Mode drivingMode = new DrivingMode();
	public Mode eventMode = new EventMode();
	public Mode placeMode = new PlaceMode();

	private Mode actualMode = null;

	private ModeManager() {

	}

	public void initListeners(Context context) {

		Log.i(TAG, "initListeners : initialise mode listeners");
		if (SettingManager.settingPreferences == null) {
			SettingManager.getINSTANCE().context = context;
			SettingManager.getINSTANCE().init(
					SettingManager.SETTING_PREFERENCES_NAME);
		}

		// load saved mode
		// String current_saved_mode = SettingManager.getINSTANCE()
		// .getSettingValue(SettingManager.CURRENT_MODE, null);
		// if (current_saved_mode != null) {
		// if (current_saved_mode.equals(sleepingMode.toString())) {
		// actualMode = sleepingMode;
		// Log.i("CurrentSavedMode", "SleepingMode");
		// } else if (current_saved_mode.equals(placeMode.toString())) {
		// actualMode = placeMode;
		// Log.i("CurrentSavedMode", "PlaceMode");
		// } else if (current_saved_mode.equals(eventMode.toString())) {
		// actualMode = eventMode;
		// Log.i("CurrentSavedMode", "EventMode");
		// } else if (current_saved_mode.equals(drivingMode.toString())) {
		// actualMode = drivingMode;
		// Log.i("CurrentSavedMode", "DrivingMode");
		// }
		//
		// }

		// init modes
		initSleepingMode(context);
		initDrivingMode(context);
		initEventMode(context);
		initPlaceMode(context);

	}

	private void initSleepingMode(Context context) {
		Log.i(TAG, "initSleepingMode : initialise sleeping mode listener");
		sleepingMode.setContext(context);
		sleepingMode.init();
		sleepingMode.setOnCheckModeListener(new IModeListener() {

			@Override
			public void onAbleForAppliance() {
				// TODO Auto-generated method stub
				if (sleepingMode.isActive)
					applyMode(sleepingMode);
			}

			@Override
			public void onDispatchMode() {
				// TODO Auto-generated method stub
				dispatchMode(sleepingMode);
			}
		});

	}

	private void initDrivingMode(Context context) {
		Log.i(TAG, "initDrivingMode : initialise driving mode listener");
		drivingMode.setContext(context);
		drivingMode.init();
		drivingMode.setOnCheckModeListener(new IModeListener() {

			@Override
			public void onAbleForAppliance() {
				// TODO Auto-generated method stub
				if (sleepingMode.isActive)
					applyMode(drivingMode);
			}

			@Override
			public void onDispatchMode() {
				// TODO Auto-generated method stub
				dispatchMode(drivingMode);
			}
		});
	}

	private void initEventMode(Context context) {
		Log.i(TAG, "initEventMode : initialise event mode listener");
		eventMode.setContext(context);
		eventMode.init();
		eventMode.setOnCheckModeListener(new IModeListener() {

			@Override
			public void onAbleForAppliance() {
				// TODO Auto-generated method stub
				if (sleepingMode.isActive)
					applyMode(eventMode);
			}

			@Override
			public void onDispatchMode() {
				// TODO Auto-generated method stub
				dispatchMode(eventMode);
			}
		});
	}

	private void initPlaceMode(Context context) {
		Log.i(TAG, "initPlaceMode : initialise place mode listener");
		placeMode.setContext(context);
		placeMode.init();
		placeMode.setOnCheckModeListener(new IModeListener() {

			@Override
			public void onAbleForAppliance() {
				// TODO Auto-generated method stub
				if (placeMode.isActive)
					applyMode(placeMode);
			}

			@Override
			public void onDispatchMode() {
				// TODO Auto-generated method stub
				dispatchMode(placeMode);
			}
		});
	}

	public void applyMode(Mode mode) {
		Log.i(TAG, "applyMode : apply " + mode);
		if (!mode.isApllied && mode.isActive && this.actualMode == null) {
			this.actualMode = mode;
			// SettingManager.getINSTANCE().modifySetting(
			// SettingManager.CURRENT_MODE, actualMode.toString());
			actualMode.apply();
		}
	}

	public void dispatchMode(Mode mode) {
		Log.i(TAG, "dispatchMode : dispatch " + mode);
		if (mode.isApllied) {
			this.actualMode = null;
			SettingManager.getINSTANCE().modifySetting(
					SettingManager.CURRENT_MODE, null);
			mode.dispatch();
		}
	}

	public void unregisterListeners() {
		Log.i(TAG, "unregisterListeners : unregistre modes listeners");
		if (actualMode != null)
			dispatchMode(actualMode);
		sleepingMode.onDestroy();
		drivingMode.onDestroy();
		placeMode.onDestroy();
		eventMode.onDestroy();
	}

	public static ModeManager getINSTANCE() {
		return INSTANCE;
	}

}
