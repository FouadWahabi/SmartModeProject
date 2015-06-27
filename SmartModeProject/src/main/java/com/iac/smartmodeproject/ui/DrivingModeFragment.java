package com.iac.smartmodeproject.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.managers.ModeManager;
import com.iac.smartmodeproject.managers.SettingManager;
import com.iac.smartmodeproject.ui.widgets.AppEditText;
import com.iac.smartmodeproject.ui.widgets.AppTextView;
import com.iac.smartmodeproject.utils.DrivingMode;

public class DrivingModeFragment extends Fragment implements
		OnCheckedChangeListener, TextWatcher {

	private AppTextView speedCounterTv;
	private CheckBox smsOption, ttsOption;
	private AppEditText drivingSms;
	private String drivingSmsString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View driving_mode_view = inflater.inflate(
				R.layout.fragment_driving_mode, container, false);

		speedCounterTv = (AppTextView) driving_mode_view
				.findViewById(R.id.speed_counter);

		drivingSms = (AppEditText) driving_mode_view
				.findViewById(R.id.driving_sms);
		drivingSms.setEnabled(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.DRIVING_MODE_SMS_OPTION, true));
		drivingSmsString = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.DRIVING_MODE_SMS_MSG,
				getString(R.string.driving_mode_sms));
		drivingSms.setText(drivingSmsString);
		drivingSms.addTextChangedListener(this);

		// creating place mode description
		AppTextView driving_mode_desc = (AppTextView) driving_mode_view
				.findViewById(R.id.driving_mode_desc);
		driving_mode_desc.setText(Html
				.fromHtml(getString(R.string.driving_mode_desc)));
		driving_mode_desc.setTextSize(16);
		driving_mode_desc.setLineSpacing(1.3f, 1);

		((DrivingMode) ModeManager.getINSTANCE().drivingMode)
				.setSpeedCounterTv(speedCounterTv);

		smsOption = (CheckBox) driving_mode_view.findViewById(R.id.enable_sms);
		ttsOption = (CheckBox) driving_mode_view.findViewById(R.id.enable_tts);

		smsOption.setChecked(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.DRIVING_MODE_SMS_OPTION, true));
		drivingSms.setEnabled(smsOption.isChecked());
		ttsOption.setChecked(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.DRIVING_MODE_TTS_OPTION, true));

		smsOption.setOnCheckedChangeListener(this);
		ttsOption.setOnCheckedChangeListener(this);

		return driving_mode_view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDetach() {
		((DrivingMode) ModeManager.getINSTANCE().drivingMode)
				.setSpeedCounterTv(null);
		super.onDetach();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		int viewId = buttonView.getId();
		switch (viewId) {
		case R.id.enable_sms:
			if (isChecked) {
				((DrivingMode) ModeManager.getINSTANCE().drivingMode)
						.enableSmsOption();
			} else {
				((DrivingMode) ModeManager.getINSTANCE().drivingMode)
						.disableSmsOption();
			}
			drivingSms.setEnabled(isChecked);

			break;
		case R.id.enable_tts:
			((DrivingMode) ModeManager.getINSTANCE().drivingMode)
					.setEnabledTtsOption(isChecked);
			break;
		default:
			break;
		}
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
		DrivingMode drivingMode = (DrivingMode) ModeManager.getINSTANCE().drivingMode;
		if (drivingMode != null) {
			drivingSmsString = s.toString();
			drivingMode.setSmsMsg(drivingSmsString);
		}
	}

}
