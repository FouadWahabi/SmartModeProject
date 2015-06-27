package com.iac.smartmodeproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.helpers.PlaceDBHelper;
import com.iac.smartmodeproject.helpers.PlaceListAdapter;
import com.iac.smartmodeproject.helpers.SQLiteCursorLoader;

public class PlaceListActivity extends Activity implements OnClickListener,
		LoaderCallbacks<Cursor>, OnItemLongClickListener {

	private String TAG = "PlaceListActivity";
	private RelativeLayout pb;
	private ListView placeList;
	private SQLiteCursorLoader loader;
	private PlaceDBHelper db;
	private PlaceListAdapter mAdapter;
	private AlertDialog.Builder dgBuilder;
	private int selectedItem = -1;
	// place list options
	private final int INFO_OPTION = 0;
	private final int DELETE_OPTION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_saved_places);
		((RelativeLayout) findViewById(R.id.no_saved_place))
				.setVisibility(View.GONE);
		// progress bar
		pb = (RelativeLayout) findViewById(R.id.progress_bar);
		pb.setVisibility(View.VISIBLE);
		// init list view
		placeList = (ListView) findViewById(R.id.saved_places);
		mAdapter = new PlaceListAdapter(getApplicationContext(), null, false);
		placeList.setAdapter(mAdapter);
		placeList.setOnItemLongClickListener(this);

		// adding urgent phone number
		ImageButton addPlace = (ImageButton) findViewById(R.id.add_place);
		addPlace.setOnClickListener(this);
		// create db
		db = new PlaceDBHelper(this);

		// load place list
		loader = (SQLiteCursorLoader) getLoaderManager().initLoader(0, null,
				this);

		// creating option dialog
		dgBuilder = new Builder(this).setItems(R.array.phone_list_options,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (selectedItem != -1) {
							switch (which) {
							case DELETE_OPTION:
								if (loader != null) {
									Log.i(TAG, mAdapter.getItemId(which) + "");
									loader.delete(
											PlaceDBHelper.TABLE_NAME,
											PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_ID]
													+ " = ?",
											new String[] { mAdapter
													.getItemId(selectedItem)
													+ "" });
									mAdapter.notifyDataSetChanged();
								}
								selectedItem = -1;
								break;
							case INFO_OPTION:
								startActivity(new Intent(
										PlaceListActivity.this,
										PlaceInfoActivity.class)
										.putExtra(
												PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LATT],
												((Cursor) mAdapter
														.getItem(selectedItem))
														.getDouble(PlaceDBHelper.COLUMN_PLACE_LATT))
										.putExtra(
												PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LANG],
												((Cursor) mAdapter
														.getItem(selectedItem))
														.getDouble(PlaceDBHelper.COLUMN_PLACE_LANG))
										.putExtra(
												PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_RADIUS],
												((Cursor) mAdapter
														.getItem(selectedItem))
														.getInt(PlaceDBHelper.COLUMN_PLACE_RADIUS)));
								selectedItem = -1;
								break;
							}
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		// adding places
		startActivityForResult(new Intent(this, AddPlaceActivity.class), 600);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String sql = "SELECT * FROM " + PlaceDBHelper.TABLE_NAME;
		return new SQLiteCursorLoader(this, db, sql, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (pb != null) {
			if (pb.getParent() != null)
				((ViewGroup) pb.getParent()).removeView(pb);
		}
		if (mAdapter != null)
			mAdapter.swapCursor(data);
		if (data.getCount() <= 0) {
			((RelativeLayout) findViewById(R.id.no_saved_place))
					.setVisibility(View.VISIBLE);
		} else {
			((RelativeLayout) findViewById(R.id.no_saved_place))
					.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (mAdapter != null) {
			mAdapter.swapCursor(null);
		}
	}

	@Override
	public void onBackPressed() {
		if (loader != null)
			onLoaderReset(loader);
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("OnActivityResult", resultCode + "");
		if (resultCode == RESULT_OK) {
			Toast.makeText(this, "Place added", Toast.LENGTH_SHORT).show();
			ContentValues values = new ContentValues();
			values.put(
					PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LABEL],
					data.getStringExtra(PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LABEL]));
			values.put(
					PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LATT],
					data.getDoubleExtra(
							PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LATT],
							-1));
			values.put(
					PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LANG],
					data.getDoubleExtra(
							PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LANG],
							-1));
			values.put(
					PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_RADIUS],
					data.getStringExtra(PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_RADIUS]));
			if (values
					.getAsDouble(PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LANG]) != -1) {
				loader.insert(PlaceDBHelper.TABLE_NAME, null, values);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		selectedItem = position;
		dgBuilder.create().show();
		return true;
	}
}
