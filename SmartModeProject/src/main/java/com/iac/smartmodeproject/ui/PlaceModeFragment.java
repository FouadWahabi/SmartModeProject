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
import com.iac.smartmodeproject.utils.PlaceMode;

public class PlaceModeFragment extends Fragment implements
		OnCheckedChangeListener, TextWatcher {

	private CheckBox smsOption;
	private AppEditText placeSms;
	private String placeSmsString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View place_mode_view = inflater.inflate(R.layout.fragment_place_mode,
				container, false);

		placeSms = (AppEditText) place_mode_view.findViewById(R.id.place_sms);
		placeSms.setEnabled(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.PLACE_MODE_SMS_OPTION, false));
		placeSmsString = SettingManager.getINSTANCE().getSettingValue(
				SettingManager.PLACE_MODE_SMS_MSG,
				getString(R.string.place_mode_sms));
		placeSms.setText(placeSmsString);
		placeSms.addTextChangedListener(this);

		// creating place mode description
		AppTextView place_mode_desc = (AppTextView) place_mode_view
				.findViewById(R.id.place_mode_desc);
		place_mode_desc.setText(Html
				.fromHtml(getString(R.string.place_mode_desc)));
		place_mode_desc.setTextSize(16);
		place_mode_desc.setLineSpacing(1.3f, 1);

		// adding tracked places
		AppButton addPlaceList = (AppButton) place_mode_view
				.findViewById(R.id.place_list);
		addPlaceList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), PlaceListActivity.class));
			}
		});

		smsOption = (CheckBox) place_mode_view.findViewById(R.id.enable_sms);
		smsOption.setChecked(SettingManager.getINSTANCE().getSettingValue(
				SettingManager.EVENT_MODE_SMS_OPTION, true));
		smsOption.setOnCheckedChangeListener(this);
		placeSms.setEnabled(smsOption.isChecked());

		return place_mode_view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			((PlaceMode) ModeManager.getINSTANCE().placeMode).enableSmsOption();
		} else {
			((PlaceMode) ModeManager.getINSTANCE().placeMode)
					.disableSmsOption();
		}
		placeSms.setEnabled(isChecked);
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
		PlaceMode placeMode = (PlaceMode) ModeManager.getINSTANCE().placeMode;
		if (placeMode != null) {
			placeSmsString = s.toString();
			placeMode.setSmsMsg(placeSmsString);
		}
	}
}
