package com.iac.smartmodeproject.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.ui.widgets.AppTextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about_activity);
		((AppTextView) findViewById(R.id.fork)).setText(Html
				.fromHtml(getString(R.string.fork)));
		((AppTextView) findViewById(R.id.fork))
				.setMovementMethod(LinkMovementMethod.getInstance());
	}

}
