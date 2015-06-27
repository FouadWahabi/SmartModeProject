package com.iac.smartmodeproject.helpers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.ui.widgets.AppTextView;

public class PlaceListAdapter extends CursorAdapter {

	private LayoutInflater inflater;

	public PlaceListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.place_list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((AppTextView) view.findViewById(R.id.place_title)).setText(cursor
				.getString(PlaceDBHelper.COLUMN_PLACE_LABEL));
		((AppTextView) view.findViewById(R.id.place_latt)).setText(cursor
				.getString(PlaceDBHelper.COLUMN_PLACE_LATT));
		((AppTextView) view.findViewById(R.id.place_long)).setText(cursor
				.getString(PlaceDBHelper.COLUMN_PLACE_LANG));
	}
}
