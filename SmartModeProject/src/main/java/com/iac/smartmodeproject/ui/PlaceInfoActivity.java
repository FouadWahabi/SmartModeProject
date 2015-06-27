package com.iac.smartmodeproject.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.helpers.PlaceDBHelper;

public class PlaceInfoActivity extends Activity implements OnMapReadyCallback {

	private double lattitude = -1;
	private double longitude = -1;
	private int radius = -1;
	private MapFragment mapFragemnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_place_info);
		lattitude = getIntent().getExtras().getDouble(
				PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LATT]);
		longitude = getIntent().getExtras().getDouble(
				PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_LANG]);
		radius = getIntent().getExtras().getInt(
				PlaceDBHelper.COLUMNS[PlaceDBHelper.COLUMN_PLACE_RADIUS]);
		mapFragemnt = (MapFragment) getFragmentManager().findFragmentById(
				R.id.map_info);
		mapFragemnt.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		if (lattitude != -1 && longitude != -1 && radius != -1) {
			googleMap.addMarker(new MarkerOptions().position(new LatLng(
					lattitude, longitude)));
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(lattitude, longitude), 16f));
			CircleOptions circleOptions = new CircleOptions();
			circleOptions.center(new LatLng(lattitude, longitude));
			circleOptions.radius(radius);
			circleOptions.strokeWidth(0f);
			circleOptions.fillColor(0xaacc0000);
			googleMap.addCircle(circleOptions);
		}
	}
}
