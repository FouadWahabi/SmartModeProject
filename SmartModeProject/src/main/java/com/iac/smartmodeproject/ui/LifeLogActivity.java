package com.iac.smartmodeproject.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.helpers.LifeLogAdapter;
import com.iac.smartmodeproject.managers.SettingManager;

public class LifeLogActivity extends Activity {

	private ListView lifeLog;
	private List<String[]> list;
	private LifeLogAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_life_log);

		lifeLog = (ListView) findViewById(R.id.life_log);
		list = SettingManager.getINSTANCE().readLog();
		findViewById(R.id.progress_bar).setVisibility(View.GONE);
		mAdapter = new LifeLogAdapter(this, list);
		lifeLog.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
}
