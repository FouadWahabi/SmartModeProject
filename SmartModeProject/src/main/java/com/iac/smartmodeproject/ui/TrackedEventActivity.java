package com.iac.smartmodeproject.ui;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.helpers.EventListAdapter;

public class TrackedEventActivity extends Activity implements
		LoaderCallbacks<Cursor>, OnItemClickListener {

	private EventListAdapter mAdapter;
	private Loader<Cursor> loader;
	private RelativeLayout pb;
	private ListView trackedEventList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_tracked_events);
		((RelativeLayout) findViewById(R.id.no_event)).setVisibility(View.GONE);
		// progress bar
		pb = (RelativeLayout) findViewById(R.id.progress_bar);
		pb.setVisibility(View.VISIBLE);

		// init list view
		trackedEventList = (ListView) findViewById(R.id.tracked_event_list);
		mAdapter = new EventListAdapter(getApplicationContext(), null, false);
		trackedEventList.setAdapter(mAdapter);
		trackedEventList.setOnItemClickListener(this);

		// load events
		loader = getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		Uri baseUri = CalendarContract.Events.CONTENT_URI;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long time = cal.getTimeInMillis();
		return new CursorLoader(this, baseUri, new String[] {
				CalendarContract.Events._ID, CalendarContract.Events.TITLE,
				CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND,
				CalendarContract.Events.AVAILABILITY }, "( "
				+ CalendarContract.Events.DTSTART + " > " + time + ")", null,
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		if (pb != null) {
			if (pb.getParent() != null)
				((ViewGroup) pb.getParent()).removeView(pb);
		}
		if (mAdapter != null)
			mAdapter.swapCursor(data);
		if (data.getCount() <= 0) {
			((RelativeLayout) findViewById(R.id.no_event))
					.setVisibility(View.VISIBLE);
		} else {
			((RelativeLayout) findViewById(R.id.no_event))
					.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		if (mAdapter != null)
			mAdapter.swapCursor(null);
	}

	@Override
	public void onBackPressed() {
		if (loader != null)
			onLoaderReset(loader);
		super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i("", "Item clicked");
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.track_event);
		checkBox.setChecked(!checkBox.isChecked());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.tracked_event_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.add_all_tracked_events) {
			for (int i = 0; i < mAdapter.getCount(); i++) {
				if (!((CheckBox) ((trackedEventList.getChildAt(i))
						.findViewById(R.id.track_event))).isChecked())
					((CheckBox) ((trackedEventList.getChildAt(i))
							.findViewById(R.id.track_event))).setChecked(true);
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
