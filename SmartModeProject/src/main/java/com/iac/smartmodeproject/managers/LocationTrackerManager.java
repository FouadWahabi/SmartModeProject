package com.iac.smartmodeproject.managers;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.iac.smartmodeproject.listeners.ILocationTrackingListener;

public class LocationTrackerManager implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {

	private String TAG = "LocationTrackerManager";
	public Context context;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private HandlerThread locationTrackerThread;
	private List<ILocationTrackingListener> listeners = new LinkedList<ILocationTrackingListener>();
	private Handler mainHandler;
	private Location lastLoc = null;
	private long lastTime = -1;
	private double speed = -1;
	public static final int KMPH_IN_METERS_PER_MSECOND = 3600;
	private boolean locationUpdates = false;

	private long locationTrackingInterval = 45000; // default location interval
	private boolean isConnecting = false;

	public LocationTrackerManager(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public void init() {

		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(context)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API).build();
		}
		if (mLocationRequest == null) {
			mLocationRequest = new LocationRequest();
			mLocationRequest.setInterval(locationTrackingInterval);
			mLocationRequest.setFastestInterval(locationTrackingInterval);
			mLocationRequest
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		}
		if (locationTrackerThread == null) {
			locationTrackerThread = new HandlerThread(TAG);
		}

	}

	public void startLocationTracking() {
		if (mLocationRequest == null || mGoogleApiClient == null
				|| locationTrackerThread == null)
			init();
		if (!mGoogleApiClient.isConnected() && !isConnecting) {
			isConnecting = true;
			mGoogleApiClient.connect();
		}
		if (!locationTrackerThread.isAlive())
			locationTrackerThread.start();
	}

	public void stopLocationUpdates() {
		if (locationTrackerThread.isAlive())
			locationTrackerThread.interrupt();
		if (mGoogleApiClient.isConnected() && locationUpdates) {
			LocationServices.FusedLocationApi.removeLocationUpdates(
					mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		isConnecting = false;
		Toast.makeText(context, "Google play services connection failed !",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		isConnecting = false;
		if (mGoogleApiClient.isConnected() && locationUpdates) {
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this,
					locationTrackerThread.getLooper());
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		isConnecting = false;
		startLocationTracking();
	}

	@Override
	public void onLocationChanged(final Location location) {
		Log.i(TAG, "Location changed");
		if (mainHandler == null)
			mainHandler = new Handler(context.getMainLooper());
		if (!location.hasSpeed()) {
			speed = location.getSpeed() * KMPH_IN_METERS_PER_MSECOND;
			Log.i(TAG, "Has speed : " + speed);
		} else {
			if (lastLoc == null) {
				lastLoc = new Location(location);
				lastTime = System.currentTimeMillis();
			} else {
				float disatnce = lastLoc.distanceTo(location);
				speed = (disatnce / (System.currentTimeMillis() - lastTime))
						* KMPH_IN_METERS_PER_MSECOND;
				Log.i(TAG,
						"Distance en metre : " + disatnce
								+ " time interval in hour : "
								+ ((System.currentTimeMillis() - lastTime)));
				Log.i(TAG, "Speed remained : " + speed);
				lastLoc.set(location);
				lastTime = System.currentTimeMillis();
			}
		}
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (ILocationTrackingListener listener : listeners) {
					listener.onLocationChanged(location);
					if (speed >= 0) {
						listener.onSpeedChanged(speed);
					}
				}
			}
		});

	}

	public void addLocationTrackerListener(ILocationTrackingListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void setLocationTrackingInterval(int locationTrackingInterval) {
		this.locationTrackingInterval = locationTrackingInterval;
		if (mLocationRequest != null) {
			mLocationRequest.setInterval(locationTrackingInterval);
			mLocationRequest.setFastestInterval(locationTrackingInterval);
		}
	}

	public Location getCurrentLocation() {
		if (mGoogleApiClient != null)
			return LocationServices.FusedLocationApi
					.getLastLocation(mGoogleApiClient);
		return null;
	}

	public void setLocationUpdates(boolean locationUpdates) {
		this.locationUpdates = locationUpdates;
	}

}
