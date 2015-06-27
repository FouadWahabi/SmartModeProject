package com.iac.smartmodeproject.ui;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.managers.ModeManager;
import com.iac.smartmodeproject.managers.SettingManager;
import com.iac.smartmodeproject.utils.SleepingMode;

public class UrgentPhoneNumbersActivity extends Activity implements
		OnClickListener, OnItemLongClickListener {

	private String TAG = "UrgentPhoneNumbersActivity";
	private List<String> urgentPhoneNumbers;
	private ArrayAdapter<String> mAdapter;
	private AlertDialog.Builder dgBuilder;
	private int selectedPhoneNumber = -1;

	// phone numbre options
	private final int INFO_OPTION = 0;
	private final int DELETE_OPTION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_urgent_phone_numbers);
		dgBuilder = new Builder(this).setItems(R.array.phone_list_options,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i(TAG, "Selected item" + which);
						if (selectedPhoneNumber != -1) {
							switch (which) {
							case DELETE_OPTION:
								deletePhoneNumbre(urgentPhoneNumbers
										.get(selectedPhoneNumber));
								break;
							case INFO_OPTION:
								Uri uri = Uri.withAppendedPath(
										PhoneLookup.CONTENT_FILTER_URI,
										Uri.encode(urgentPhoneNumbers
												.get(selectedPhoneNumber)));
								Cursor contactDetails = getContentResolver()
										.query(uri,
												new String[] { PhoneLookup.DISPLAY_NAME },
												null, null, null);
								contactDetails.moveToFirst();
								AlertDialog infoDialog = new AlertDialog.Builder(
										UrgentPhoneNumbersActivity.this)
										.setMessage(contactDetails.getString(0))
										.create();
								infoDialog
										.requestWindowFeature(Window.FEATURE_NO_TITLE);
								infoDialog.show();
								break;
							}
							selectedPhoneNumber = -1;
						}
					}
				});
		urgentPhoneNumbers = new LinkedList<String>();
		JSONArray json_array;
		try {
			json_array = new JSONArray(
					SettingManager.settingPreferences.getString(
							SettingManager.URGENT_CALLS, "[]"));
			if (json_array != null) {
				for (int i = 0; i < json_array.length(); i++) {
					urgentPhoneNumbers.add(json_array.getString(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ListView urgentPhoneList = (ListView) findViewById(R.id.urgent_phone_numbers);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_activated_1);
		urgentPhoneList.setAdapter(mAdapter);
		urgentPhoneList.setOnItemLongClickListener(this);
		mAdapter.addAll(urgentPhoneNumbers);
		mAdapter.notifyDataSetChanged();

		// adding urgent phone number
		ImageButton addUrgentPhoneNumber = (ImageButton) findViewById(R.id.add_urgent_phone_number);

		addUrgentPhoneNumber.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent cantactIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
		startActivityForResult(cantactIntent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == RESULT_OK) {
			Cursor cr = getContentResolver().query(data.getData(), null, null,
					null, null);
			if (cr != null && cr.moveToFirst()) {
				if (cr.getColumnIndex(Phone.NUMBER) != -1) {
					addPhoneNumbre(cr
							.getString(cr.getColumnIndex(Phone.NUMBER)));
				}
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		selectedPhoneNumber = position;
		dgBuilder.create().show();
		return true;
	}

	private void addPhoneNumbre(String pickedPhone) {
		if (urgentPhoneNumbers != null && !pickedPhone.isEmpty()) {
			if (!urgentPhoneNumbers.contains(pickedPhone)) {
				urgentPhoneNumbers.add(pickedPhone);
				if ((SleepingMode) ModeManager.getINSTANCE().sleepingMode != null) {
					if (((SleepingMode) ModeManager.getINSTANCE().sleepingMode).urgentPhoneNumbers != null)
						((SleepingMode) ModeManager.getINSTANCE().sleepingMode).urgentPhoneNumbers
								.add(pickedPhone);
					else {
						((SleepingMode) ModeManager.getINSTANCE().sleepingMode)
								.init();
						((SleepingMode) ModeManager.getINSTANCE().sleepingMode).urgentPhoneNumbers
								.add(pickedPhone);
					}

					String json = (new JSONArray(urgentPhoneNumbers))
							.toString();
					SettingManager.getINSTANCE().modifySetting(
							SettingManager.URGENT_CALLS, json);
					mAdapter.add(pickedPhone);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void deletePhoneNumbre(String phoneNumbre) {
		if (urgentPhoneNumbers != null) {
			if (urgentPhoneNumbers.contains(phoneNumbre)) {
				urgentPhoneNumbers.remove(phoneNumbre);
				if ((SleepingMode) ModeManager.getINSTANCE().sleepingMode != null) {
					if (((SleepingMode) ModeManager.getINSTANCE().sleepingMode).urgentPhoneNumbers != null)
						((SleepingMode) ModeManager.getINSTANCE().sleepingMode).urgentPhoneNumbers
								.remove(phoneNumbre);
					else {
						((SleepingMode) ModeManager.getINSTANCE().sleepingMode)
								.init();
						((SleepingMode) ModeManager.getINSTANCE().sleepingMode).urgentPhoneNumbers
								.remove(phoneNumbre);
					}

					String json = (new JSONArray(urgentPhoneNumbers))
							.toString();
					SettingManager.getINSTANCE().modifySetting(
							SettingManager.URGENT_CALLS, json);
					mAdapter.remove(phoneNumbre);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

}
