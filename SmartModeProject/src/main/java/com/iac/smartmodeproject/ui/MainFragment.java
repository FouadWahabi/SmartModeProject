package com.iac.smartmodeproject.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.ui.widgets.AppTextView;

public class MainFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View main_view = inflater.inflate(R.layout.fragment_main, container,
				false);
		((AppTextView) main_view.findViewById(R.id.app_desc)).setText(Html
				.fromHtml(getString(R.string.app_desc)));
		return main_view;
	}
}
