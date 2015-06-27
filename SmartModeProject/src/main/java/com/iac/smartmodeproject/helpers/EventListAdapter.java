package com.iac.smartmodeproject.helpers;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.ui.widgets.AppTextView;

public class EventListAdapter extends CursorAdapter {

	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_EVENT_TITLE_INDEX = 1;
	private static final int PROJECTION_DTSTART_INDEX = 2;
	private static final int PROJECTION_DTEND_INDEX = 3;
	private static final int PROJECTION_AVAILABILITY_INDEX = 4;

	LayoutInflater inflater;
	ContentResolver cr;
	Calendar cl, _cl, __cl;

	public EventListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		cr = context.getContentResolver();
		cl = Calendar.getInstance();
		_cl = Calendar.getInstance();
		__cl = Calendar.getInstance();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.event_list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (cursor != null) {
			final String eventId = cursor.getString(PROJECTION_ID_INDEX);
			final int availibilty = cursor
					.getInt(PROJECTION_AVAILABILITY_INDEX);
			((AppTextView) view.findViewById(R.id.event_title)).setText(cursor
					.getString(PROJECTION_EVENT_TITLE_INDEX));
			__cl.setTimeInMillis(System.currentTimeMillis());
			cl.setTimeInMillis(cursor.getLong(PROJECTION_DTSTART_INDEX));
			_cl.setTimeInMillis(cursor.getLong(PROJECTION_DTEND_INDEX));

			if (cl.get(Calendar.DAY_OF_MONTH) == __cl
					.get(Calendar.DAY_OF_MONTH)
					&& cl.get(Calendar.MONTH) == __cl.get(Calendar.MONTH)
					&& cl.get(Calendar.YEAR) == __cl.get(Calendar.YEAR)
					&& _cl.get(Calendar.DAY_OF_MONTH) == __cl
							.get(Calendar.DAY_OF_MONTH)
					&& _cl.get(Calendar.MONTH) == __cl.get(Calendar.MONTH)
					&& _cl.get(Calendar.YEAR) == __cl.get(Calendar.YEAR)) {
				((AppTextView) view.findViewById(R.id.event_start_time))
						.setText(String.format("Today at %1$tR ", cl));

				if (cl.get(Calendar.DAY_OF_MONTH) == _cl
						.get(Calendar.DAY_OF_MONTH)
						&& cl.get(Calendar.MONTH) == _cl.get(Calendar.MONTH)
						&& cl.get(Calendar.YEAR) == _cl.get(Calendar.YEAR))

					((AppTextView) view.findViewById(R.id.event_end_time))
							.setText(String.format(" %1$tR", _cl));
			} else {
				__cl.add(Calendar.DAY_OF_MONTH, 1);
				if (cl.get(Calendar.DAY_OF_MONTH) == __cl
						.get(Calendar.DAY_OF_MONTH)
						&& cl.get(Calendar.MONTH) == __cl.get(Calendar.MONTH)
						&& cl.get(Calendar.YEAR) == __cl.get(Calendar.YEAR)
						&& cl.get(Calendar.DAY_OF_MONTH) == _cl
								.get(Calendar.DAY_OF_MONTH)
						&& cl.get(Calendar.MONTH) == _cl.get(Calendar.MONTH)
						&& cl.get(Calendar.YEAR) == _cl.get(Calendar.YEAR)) {
					__cl.setTimeInMillis(System.currentTimeMillis());
					((AppTextView) view.findViewById(R.id.event_start_time))
							.setText(String.format("Tomorrow at %1$tR ", cl));

					((AppTextView) view.findViewById(R.id.event_end_time))
							.setText(String.format(" %1$tR", _cl));
				} else {
					__cl.setTimeInMillis(System.currentTimeMillis());
					if (cl.get(Calendar.YEAR) == __cl.get(Calendar.YEAR)) {
						((AppTextView) view.findViewById(R.id.event_start_time))
								.setText(String.format("%1$ta, %1$tb %1$tR ",
										cl));
						if (cl.get(Calendar.DAY_OF_MONTH) == _cl
								.get(Calendar.DAY_OF_MONTH)
								&& cl.get(Calendar.MONTH) == _cl
										.get(Calendar.MONTH)
								&& cl.get(Calendar.YEAR) == _cl
										.get(Calendar.YEAR)) {
							((AppTextView) view
									.findViewById(R.id.event_end_time))
									.setText(String.format(" %1$tR", _cl));
						} else {
							((AppTextView) view
									.findViewById(R.id.event_end_time))
									.setText(String.format(
											" %1$ta, %1$tb %1$tR", _cl));
						}

					} else {

						((AppTextView) view.findViewById(R.id.event_start_time))
								.setText(String.format(
										"%1$ta, %1$tb %1$ty %1$tR ", cl));

						_cl.setTimeInMillis(cursor
								.getLong(PROJECTION_DTEND_INDEX));

						((AppTextView) view.findViewById(R.id.event_end_time))
								.setText(String.format(
										" %1$ta, %1$tb %1$ty %1$tR", _cl));

					}

				}

			}

			CheckBox trackEvent = (CheckBox) view
					.findViewById(R.id.track_event);
			if (availibilty == Events.AVAILABILITY_BUSY) {
				Cursor c = cr.query(CalendarContract.Reminders.CONTENT_URI,
						new String[] { CalendarContract.Reminders.MINUTES },
						"(" + CalendarContract.Reminders.EVENT_ID + " = ?)",
						new String[] { eventId }, null);
				while (c.moveToNext()) {
					if (c.getLong(0) == 0) {
						trackEvent.setChecked(true);
						break;
					}
				}
			}
			trackEvent
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								Log.i("", "Add " + eventId + " to track list");
								ContentValues values = new ContentValues();
								values.put(Reminders.MINUTES, 0);
								values.put(Reminders.EVENT_ID, eventId);
								values.put(Reminders.METHOD,
										Reminders.METHOD_ALERT);
								cr.insert(Reminders.CONTENT_URI, values);
								values = new ContentValues();
								values.put(Events.AVAILABILITY,
										Events.AVAILABILITY_BUSY);
								if (availibilty != Events.AVAILABILITY_BUSY) {
									cr.update(ContentUris.withAppendedId(
											Events.CONTENT_URI,
											Long.parseLong(eventId)), values,
											null, null);
								}
							} else {
								Log.i("", "Remove " + eventId
										+ " to track list");
								cr.delete(Reminders.CONTENT_URI, "("
										+ Reminders.EVENT_ID + " = ? ) AND ( "
										+ Reminders.MINUTES + " = ? )",
										new String[] { eventId, "0" });
							}
						}
					});
		}
	}

}
