package com.iac.smartmodeproject.managers;

import java.util.LinkedList;
import java.util.List;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.LocationRequest;
import com.iac.smartmodeproject.listeners.IActivityRencognitionListener;

public class ActivityRecognitionManager implements ConnectionCallbacks,
		OnConnectionFailedListener {

	private String TAG = "ActivityRecognitionManager";
	private static ActivityRecognitionManager INSTANCE;
	public Context context;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private List<IActivityRencognitionListener> listsners = new LinkedList<IActivityRencognitionListener>();

	private long detectionIntervalMillis = 0;

	private BroadcastReceiver activityRecognitionReceiver;
	private String ACTIVITY_RECOGNITION_ACTION = "tn.iac.intent.action.ACTIVITY_RECOGNITION_RECEIVER";
	private PendingIntent activityRecognitionPI;
	private boolean isConnecting = false;
	private boolean isConnected = false;

	private ActivityRecognitionManager() {

	}

	private ActivityRecognitionManager(Context context) {
		this.context = context;
		init();
	}

	public void init() {
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(context)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(ActivityRecognition.API).build();
		}
		activityRecognitionPI = PendingIntent.getBroadcast(context, 0,
				new Intent(ACTIVITY_RECOGNITION_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		activityRecognitionReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ACTIVITY_RECOGNITION_ACTION)) {
					if (ActivityRecognitionResult.hasResult(intent)) {
						ActivityRecognitionResult result = ActivityRecognitionResult
								.extractResult(intent);
						for (IActivityRencognitionListener listener : listsners)
							listener.onActivityStateChanged(result);
					} else {
						Log.i(TAG, "There's no result !");
					}
				}
			}
		};
		context.registerReceiver(activityRecognitionReceiver, new IntentFilter(
				ACTIVITY_RECOGNITION_ACTION));
	}

	public void startActivityRecognitionTracking() {
		if (mGoogleApiClient == null) {
			init();
		}
		if (!mGoogleApiClient.isConnected() && !isConnecting) {
			isConnected = false;
			isConnecting = true;
			mGoogleApiClient.connect();
		}
	}

	public void stopActivityRecognitionTracking() {
		if (mGoogleApiClient != null && activityRecognitionPI != null
				&& mGoogleApiClient.isConnected() && !isConnecting
				&& isConnected) {
			ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
					mGoogleApiClient, activityRecognitionPI);
			mGoogleApiClient.disconnect();
			isConnected = false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		isConnecting = false;
		isConnected = false;
		Toast.makeText(context, "Google play services connection failed !",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "well connected");
		isConnecting = false;
		if (mGoogleApiClient.isConnected()) {
			com.google.android.gms.common.api.PendingResult<com.google.android.gms.common.api.Status> pendingResult = ActivityRecognition.ActivityRecognitionApi
					.requestActivityUpdates(mGoogleApiClient,
							detectionIntervalMillis, activityRecognitionPI);
			pendingResult.setResultCallback(new ResultCallback<Status>() {

				@Override
				public void onResult(Status result) {
					if (result.isSuccess()) {
						isConnected = true;
					} else {
						isConnected = false;
					}
				}
			});
		} else {
			isConnected = false;
			Toast.makeText(context, "Connection failed !", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void onConnectionSuspended(int cause) {
		isConnecting = false;
		startActivityRecognitionTracking();
	}

	public void addActivityRecognitionListener(
			IActivityRencognitionListener listener) {
		listsners.add(listener);
	}

	public static ActivityRecognitionManager getINSTANCE(Context context) {
		return INSTANCE == null ? new ActivityRecognitionManager(context)
				: INSTANCE;
	}

	public void setDetectionIntervalMillis(long detectionIntervalMillis) {
		this.detectionIntervalMillis = detectionIntervalMillis;
	}

}
