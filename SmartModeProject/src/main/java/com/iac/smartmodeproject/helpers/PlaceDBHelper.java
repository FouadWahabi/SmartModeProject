package com.iac.smartmodeproject.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "place";
	public static final String[] COLUMNS = new String[] { "_id", "place_label",
			"place_latt", "place_lang", "place_radius" };

	private static final String DATABASE_NAME = "places.db";
	private static final int DATABASE_VERSION = 1;

	public static final int COLUMN_ID = 0;
	public static final int COLUMN_PLACE_LABEL = 1;
	public static final int COLUMN_PLACE_LATT = 2;
	public static final int COLUMN_PLACE_LANG = 3;
	public static final int COLUMN_PLACE_RADIUS = 4;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_NAME
			+ "( " + COLUMNS[COLUMN_ID]
			+ " integer primary key autoincrement, "
			+ COLUMNS[COLUMN_PLACE_LABEL] + " text not null, "
			+ COLUMNS[COLUMN_PLACE_LATT] + " double not null, "
			+ COLUMNS[COLUMN_PLACE_LANG] + " double not null, "
			+ COLUMNS[COLUMN_PLACE_RADIUS] + " integer not null );";

	public PlaceDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
