package com.iac.smartmodeproject.managers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class SettingManager {

	private String TAG = "SettingManger";
	public Context context;
	private static SettingManager INSTANCE = new SettingManager();
	public static SharedPreferences settingPreferences;
	public static String SETTING_PREFERENCES_NAME = "SettingPreferences";
	private Editor settingPreferencesEditor;
	private final String LOG_FILE_NAME = "life_log";
	private FileOutputStream lifeLog;

	// settings fields
	public static int SLEEPING_MODE = 0;
	public static int EVENT_MODE = 1;
	public static int PLACE_MODE = 2;
	public static int DRIVING_MODE = 3;

	public static String SETTINGS_INITIATED = "settings_initiated";
	// enabled/disabled sleeping mode
	public static String ENABLED_DISABLED_SLEEPING_MODE = "EnabledDisabledSLeepingMode";

	// enabled/disabled sleeping mode
	public static String ENABLED_DISABLED_DRIVING_MODE = "EnabledDisabledDrivingMode";

	// enabled/disabled sleeping mode
	public static String ENABLED_DISABLED_EVENT_MODE = "EnabledDisabledEventMode";

	// enabled/disabled sleeping mode
	public static String ENABLED_DISABLED_PLACE_MODE = "EnabledDisabledPlaceMode";

	// to store the sleeping nominal time
	public static String SLEEPING_TIME = "SleepingTime";

	// to store the list of urgent calls
	public static String URGENT_CALLS = "UrgentCalls";

	/* sleeping mode options */
	public static String SLEEPING_MODE_SMS_OPTION = "SleepingModeSmsOption";

	/* event mode options */
	public static String EVENT_MODE_SMS_OPTION = "EventModeSmsOption";

	/* place mode options */
	public static String PLACE_MODE_SMS_OPTION = "PlaceModeSmsOption";

	/* driving mode options */
	public static String DRIVING_MODE_SMS_OPTION = "DrivingModeSmsOption";

	/* driving mode options */
	public static String DRIVING_MODE_TTS_OPTION = "DrivingModeTtsOption";

	/* sleeping mode msg */
	public static String SLEEPING_MODE_SMS_MSG = "SleepingModeSmsMsg";

	/* event mode msg */
	public static String EVENT_MODE_SMS_MSG = "EventModeSmsMsg";

	/* place mode msg */
	public static String PLACE_MODE_SMS_MSG = "PlaceModeSmsMsg";

	/* driving mode msg */
	public static String DRIVING_MODE_SMS_MSG = "DrivingModeSmsMsg";

	// current mode
	public static String CURRENT_MODE = "CurrentMode";

	private List<String> urgentPhone = new LinkedList<String>();

	private SettingManager() {
		// TODO Auto-generated constructor stub
	}

	public void init(final String preferenceName) {

		settingPreferences = context.getSharedPreferences(preferenceName,
				Context.MODE_PRIVATE);
		settingPreferencesEditor = settingPreferences.edit();

	}

	public void modifySetting(final String settingElement, final String value) {
		settingPreferencesEditor.putString(settingElement, value);
		settingPreferencesEditor.commit();
	}

	public void modifySetting(final String settingElement, final int value) {
		settingPreferencesEditor.putInt(settingElement, value);
		settingPreferencesEditor.commit();
	}

	public void modifySetting(final String settingElement, final boolean value) {
		settingPreferencesEditor.putBoolean(settingElement, value);
		settingPreferencesEditor.commit();
	}

	public void modifySetting(final String settingElement, final long value) {
		settingPreferencesEditor.putLong(settingElement, value);
		settingPreferencesEditor.commit();
	}

	public boolean getSettingValue(final String settingMode, boolean defValue) {
		return settingPreferences.getBoolean(settingMode, defValue);
	}

	public String getSettingValue(final String settingElement, String defValue) {
		return settingPreferences.getString(settingElement, defValue);
	}

	public int getSettingValue(final String settingElement, int defValue) {
		return settingPreferences.getInt(settingElement, defValue);
	}

	public long getSettingValue(final String settingElement, long defValue) {
		return settingPreferences.getLong(settingElement, defValue);
	}

	public void addUrgentPhoneNumber(String phone) {
		urgentPhone.add(phone);
		JSONArray json_array = new JSONArray(urgentPhone);

		settingPreferencesEditor.putString("UrgentPhoneCalls",
				json_array.toString());
		settingPreferencesEditor.commit();
	}

	public void addLog(int mode, String data, long time) {
		try {
			String eol = System.getProperty("line.separator");
			lifeLog = context
					.openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);

			// writing data
			lifeLog.write((mode + eol).getBytes());
			lifeLog.write(data != null ? (data + eol).getBytes() : eol
					.getBytes());
			lifeLog.write((time + eol).getBytes());
			lifeLog.write(("$" + eol).getBytes());

		} catch (FileNotFoundException e) {
			Toast.makeText(context, "Connot open life log", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				lifeLog.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String[]> readLog() {
		String eol = System.getProperty("line.separator");
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					context.openFileInput(LOG_FILE_NAME)));
			String line;
			List<String[]> buffer = new LinkedList<String[]>();
			while ((line = input.readLine()) != null) {
				if (!line.equals("$"))
					buffer.add(new String[] { line, input.readLine(),
							input.readLine() });
			}

			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new LinkedList<String[]>();
	}

	public static SettingManager getINSTANCE() {
		return INSTANCE;
	}

}
