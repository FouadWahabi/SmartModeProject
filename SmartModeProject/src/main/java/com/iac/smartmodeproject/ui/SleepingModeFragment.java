package com.iac.smartmodeproject.ui;

import java.util.Calendar;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.managers.ModeManager;
import com.iac.smartmodeproject.managers.SettingManager;
import com.iac.smartmodeproject.ui.widgets.AppButton;
import com.iac.smartmodeproject.ui.widgets.AppEditText;
import com.iac.smartmodeproject.ui.widgets.AppTextView;
import com.iac.smartmodeproject.utils.SleepingMode;

public class SleepingModeFragment extends Fragment implements
		OnCheckedChangeListener, TextWatcher {

	public TimePickerDialog tp;
	private Calendar c = Calendar.getInstance();
	private SettingManager setting_manager = SettingManager.getINSTANCE();
	private SleepingMode sl = (SleepingMode) ModeManager.getINSTANCE().sleepingMode;
	private CheckBox smsOption;
	private AppEditText sleepingSms;
	private String sleepingSmsString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// avoid allocating memory in drawing responsible methods to avoid
		// invoking garbage collector which blocks the UI
		View sleeping_mode_view = inflater.inflate(
				R.layout.fragment_sleeping_mode, container, false);

		sleepingSms = (AppEditText) sleeping_mode_view
				.findViewById(R.id.sleeping_sms);
		sleepingSms.setEnabled(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.SLEEPING_MODE_SMS_OPTION, true));
		sleepingSmsString = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.SLEEPING_MODE_SMS_MSG,
				getString(R.string.sleeping_mode_sms));
		sleepingSms.setText(sleepingSmsString);
		sleepingSms.addTextChangedListener(this);

		TimePickerDialog.OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				c.set(Calendar.HOUR_OF_DAY, hourOfDay);
				c.set(Calendar.MINUTE, minute);
				sl.setSleepingTime(c.getTimeInMillis());
				sl.updateAlarm();
				setting_manager.modifySetting(SettingManager.SLEEPING_TIME,
						c.getTimeInMillis());
			}
		};
		tp = new TimePickerDialog(getActivity(), onTimeSetListener, 21, 0, true);
		c.set(Calendar.HOUR_OF_DAY, 21);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.setTimeInMillis(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.SLEEPING_TIME, c.getTimeInMillis()));
		tp.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));

		// creating sleeping mode description
		AppTextView sleeping_mode_desc = (AppTextView) sleeping_mode_view
				.findViewById(R.id.sleeping_mode_desc);
		sleeping_mode_desc.setText(Html
				.fromHtml(getString(R.string.sleeping_mode_desc)));
		sleeping_mode_desc.setTextSize(16);
		sleeping_mode_desc.setLineSpacing(1.3f, 1);

		// initiating settings button listeners
		AppButton set_sleep_time = (AppButton) sleeping_mode_view
				.findViewById(R.id.btn_set_sleep_time);
		set_sleep_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tp.show();
			}
		});

		AppButton add_urgent_phone_number = (AppButton) sleeping_mode_view
				.findViewById(R.id.btn_add_urgent_phone);
		add_urgent_phone_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),
						UrgentPhoneNumbersActivity.class));
			}
		});

		smsOption = (CheckBox) sleeping_mode_view.findViewById(R.id.enable_sms);
		smsOption.setChecked(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_OPTION, true));
		sleepingSms.setEnabled(smsOption.isChecked());
		smsOption.setOnCheckedChangeListener(this);

		return sleeping_mode_view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			((SleepingMode) ModeManager.getINSTANCE().sleepingMode)
					.enableSmsOption();
		} else {
			((SleepingMode) ModeManager.getINSTANCE().sleepingMode)
					.disableSmsOption();
		}
		sleepingSms.setEnabled(isChecked);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		SleepingMode sleepingMode = (SleepingMode) ModeManager.getINSTANCE().sleepingMode;
		if (sleepingMode != null) {
			sleepingSmsString = s.toString();
			sleepingMode.setSmsMsg(sleepingSmsString);
		}
	}
}
