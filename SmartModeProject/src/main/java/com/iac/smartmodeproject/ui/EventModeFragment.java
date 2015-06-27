package com.iac.smartmodeproject.ui;

import android.app.Fragment;
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

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.managers.ModeManager;
import com.iac.smartmodeproject.managers.SettingManager;
import com.iac.smartmodeproject.ui.widgets.AppButton;
import com.iac.smartmodeproject.ui.widgets.AppEditText;
import com.iac.smartmodeproject.ui.widgets.AppTextView;
import com.iac.smartmodeproject.utils.EventMode;

public class EventModeFragment extends Fragment implements
		OnCheckedChangeListener, TextWatcher {

	private CheckBox smsOption;
	private AppEditText eventSms;
	private String eventSmsString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View event_mode_view = inflater.inflate(R.layout.fragment_event_mode,
				container, false);

		eventSms = (AppEditText) event_mode_view.findViewById(R.id.event_sms);
		eventSms.setEnabled(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_OPTION, false));
		eventSmsString = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_MSG,
				getString(R.string.event_mode_sms));
		eventSms.setText(eventSmsString);
		eventSms.addTextChangedListener(this);

		// initiate event mode description
		AppTextView event_mode_desc = (AppTextView) event_mode_view
				.findViewById(R.id.event_mode_desc);
		event_mode_desc.setText(Html
				.fromHtml(getString(R.string.event_mode_desc)));
		event_mode_desc.setTextSize(16);
		event_mode_desc.setLineSpacing(1.3f, 1);

		// adding tracked events
		AppButton addTrackedEvent = (AppButton) event_mode_view
				.findViewById(R.id.btn_add_tracked_events);
		addTrackedEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),
						TrackedEventActivity.class));
			}
		});

		smsOption = (CheckBox) event_mode_view.findViewById(R.id.enable_sms);
		smsOption.setChecked(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_OPTION, true));
		smsOption.setOnCheckedChangeListener(this);
		eventSms.setEnabled(smsOption.isChecked());

		return event_mode_view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			((EventMode) ModeManager.getINSTANCE().eventMode).enableSmsOption();
		} else {
			((EventMode) ModeManager.getINSTANCE().eventMode)
					.disableSmsOption();
		}
		eventSms.setEnabled(isChecked);

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
		EventMode eventMode = (EventMode) ModeManager.getINSTANCE().eventMode;
		if (eventMode != null) {
			eventSmsString = s.toString();
			eventMode.setSmsMsg(eventSmsString);
		}
	}
}
