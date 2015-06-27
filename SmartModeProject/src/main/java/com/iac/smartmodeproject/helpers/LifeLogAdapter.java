package com.iac.smartmodeproject.helpers;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.ui.widgets.AppTextView;

public class LifeLogAdapter extends BaseAdapter {

	private List<String[]> list;
	private Context context;
	private LayoutInflater inflater;
	private int[] ic = new int[] { R.drawable.ic_sleeping_mode_log,
			R.drawable.ic_event_mode_log, R.drawable.ic_place_mode_log,
			R.drawable.ic_driving_mode_log };

	private Calendar calendar = Calendar.getInstance();

	public LifeLogAdapter(Context context, List<String[]> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(getCount() - position - 1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.life_log_item, parent, false);

		// creating views
		ImageView icView = (ImageView) itemView.findViewById(R.id.log_ic);
		icView.setImageDrawable(context.getResources().getDrawable(
				ic[Integer.parseInt(((String[]) getItem(position))[0])]));

		AppTextView log_name = (AppTextView) itemView
				.findViewById(R.id.log_name);
		int mode = Integer.parseInt(((String[]) getItem(position))[0]);

		switch (mode) {
		case 0:
			log_name.setText(context.getString(R.string.sleeping_log)
					+ ((String[]) getItem(position))[1]);
			break;

		case 1:
			log_name.setText(context.getString(R.string.event_log) + " "
					+ ((String[]) getItem(position))[1]);
			break;
		case 2:
			log_name.setText(context.getString(R.string.place_log) + " "
					+ ((String[]) getItem(position))[1]);
			break;
		case 3:
			log_name.setText(context.getString(R.string.driving_log)
					+ ((String[]) getItem(position))[1]);
			break;
		default:
			break;
		}

		AppTextView logTime = (AppTextView) itemView
				.findViewById(R.id.log_time);

		long time = Long.parseLong(((String[]) getItem(position))[2]);
		String timeString = "";

		calendar.setTimeInMillis(time);
		Calendar currentTime = Calendar.getInstance();
		if (currentTime.get(Calendar.DAY_OF_YEAR) == calendar
				.get(Calendar.DAY_OF_YEAR)) {
			timeString += "Today, ";
		} else {
			if (currentTime.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
				timeString += String.format("%1$tb, ", calendar);
			} else {
				timeString += String.format("%1$tb %1$ta, ", calendar);
			}
		}
		timeString += calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE);
		logTime.setText(timeString);

		return itemView;
	}
}
