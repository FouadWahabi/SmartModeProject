package com.iac.smartmodeproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.helpers.PlaceDBHelper;
import com.iac.smartmodeproject.managers.LocationTrackerManager;
import com.iac.smartmodeproject.ui.widgets.AppEditText;

public class AddPlaceActivity extends Activity implements OnMapReadyCallback,
		OnSeekBarChangeListener {

	private LocationTrackerManager locationTrackerManager;
	private GoogleMap googleMap;
	private SeekBar seekBar;
	private AppEditText radiusValueEt;
	private CircleOptions circleOptions = new CircleOptions();
	private Circle radiusCircle;
	private AppEditText placeTitle;
	private Location currentLocation;

	private int DEFAULT_RADIUS = 50;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_add_place);
		locationTrackerManager = new LocationTrackerManager(this);
		locationTrackerManager.startLocationTracking();
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		seekBar = (SeekBar) findViewById(R.id.place_radius_seek);
		seekBar.setProgress(DEFAULT_RADIUS);
		seekBar.setOnSeekBarChangeListener(this);

		placeTitle = (AppEditText) findViewById(R.id.place_title);
		placeTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (placeTitle.getText().toString().length() <= 0) {
					placeTitle.setError("Please entre place title !");
				}
			}
		});
		// initiating radius values
		radiusValueEt = (AppEditText) findViewById(R.id.place_radius_value);
		radiusValueEt.setText(DEFAULT_RADIUS + "");

		// creating circle options
		circleOptions.strokeWidth(0f);
		circleOptions.fillColor(0xaacc0000);

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		currentLocation = locationTrackerManager.getCurrentLocation();
		this.googleMap = googleMap;
		if (currentLocation != null && currentLocation.getAccuracy() <= 30) {
			((RelativeLayout) findViewById(R.id.current_location_error))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.place_layout_content))
					.setVisibility(View.VISIBLE);
			googleMap.addMarker(new MarkerOptions().position(new LatLng(
					currentLocation.getLatitude(), currentLocation
							.getLongitude())));
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(currentLocation.getLatitude(), currentLocation
							.getLongitude()), 16f));
			circleOptions.center(new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude()));
			circleOptions.radius(seekBar != null ? seekBar.getProgress()
					: DEFAULT_RADIUS);
			radiusCircle = googleMap.addCircle(circleOptions);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		radiusValueEt.setText(progress + "");
		radiusCircle.setRadius(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		if (placeTitle.getText().toString().length() > 0
				&& currentLocation != null
				&& currentLocation.getAccuracy() <= 30) {
			new AlertDialog.Builder(this)
					.setMessage("Are you sure you want to add this place ?")
					.setPositiveButton("Ok", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							setResult(
									RESULT_OK,
									new Intent()
											.putExtra(
													PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LABEL],
													placeTitle.getText()
															.toString())
											.putExtra(
													PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LATT],
													currentLocation
															.getLatitude())
											.putExtra(
													PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LANG],
													currentLocation
															.getLongitude())
											.putExtra(
													PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_RADIUS],
													radiusValueEt.getText()
															.toString()));
							// exit
							AddPlaceActivity.super.onBackPressed();
						}
					}).setNegativeButton("Cancel", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							setResult(RESULT_CANCELED);
							AddPlaceActivity.super.onBackPressed();
						}
					}).create().show();
		} else {
			setResult(RESULT_CANCELED);
			AddPlaceActivity.super.onBackPressed();
		}
	}
}
