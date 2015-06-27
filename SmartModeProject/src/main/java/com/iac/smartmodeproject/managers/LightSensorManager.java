package com.iac.smartmodeproject.managers;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.iac.smartmodeproject.listeners.ILightSensorListener;

public class LightSensorManager {

	private String TAG = "LightSensorManager";
	private static LightSensorManager INSTANCE = new LightSensorManager();
	private ArrayList<ILightSensorListener> lightSensorListeners = new ArrayList<ILightSensorListener>();

	private static SensorManager sensorManager;
	private static Sensor mLight;
	private SensorEventListener svListener = null;

	// bg thread for tampering light sensor data
	private HandlerThread sensorChangedHandlerThread = null;
	private Handler sensorChangedHandler;
	private Context context;
	private Handler mainHandler;

	// const
	private LightSensorManager() {

	}

	public void init(Context context) {
		Log.i(TAG, "init : init light sensor manager");
		this.context = context;
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (mLight == null) {
			Toast.makeText(context, "Light sensor not supported ",
					Toast.LENGTH_LONG).show();
		}
	}

	public boolean isLightSensorExist() {
		Log.i(TAG, "isLightSensorExist : test if light sensor exist");
		return mLight != null ? true : false;
	}

	public void registreLightSensorListener(
			ILightSensorListener lightSensorListener) {
		Log.i(TAG,
				"registreLightSensorListener : add listener to the light sensor");
		lightSensorListeners.add(lightSensorListener);
	}

	public void unregisterLightSensorListener(
			ILightSensorListener lightSensorListener) {
		Log.i(TAG,
				"unregistreLightSensorListener : delete listener from the light sensor");
		lightSensorListeners.remove(lightSensorListener);
	}

	public void startTamperingLight() {
		Log.i(TAG, "startTamperingLight : start tampering light sensor data");
		if (mLight != null) {
			svListener = new SensorEventListener() {

				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
					float lightLevel = event.values[0];
					// Log.i("LightSensorManager", lightLevel + "");
					notifyListeners(lightLevel);
				}

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub

				}
			};

			// I used Handler to make sensor events delivered in bg Thread
			// sensorEvents --> bg Thread MessageQueue --> SensorEventListener
			sensorChangedHandlerThread = new HandlerThread(
					"sensorChangedHandler");
			sensorChangedHandlerThread.start();
			sensorChangedHandler = new Handler(
					sensorChangedHandlerThread.getLooper());
			sensorManager.registerListener(svListener, mLight,
					SensorManager.SENSOR_DELAY_NORMAL, sensorChangedHandler);
		}
	}

	public void stopTampering() {
		Log.i(TAG, "stopTampering : stop tampeing light sensor data");
		if (mLight != null) {
			if ((svListener != null) && (sensorChangedHandlerThread != null)) {
				sensorManager.unregisterListener(svListener);
				sensorChangedHandlerThread.quit();
				sensorChangedHandlerThread.interrupt();
			}
		}
	}

	private void notifyListeners(final float lightLevel) {
		/*
		 * Log.i(TAG,
		 * "notifyListeners : notify all listeners with the change of light data"
		 * );
		 */
		if (context != null) {
			for (int i = 0; i < lightSensorListeners.size(); i++) {
				if (mainHandler == null) {
					mainHandler = new Handler(context.getMainLooper());
				}
				final ILightSensorListener ls = lightSensorListeners.get(i);
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						Log.d("lightLevel", lightLevel + "");
						ls.onSensorChanged(lightLevel);
					}
				});
			}
		}
	}

	public static LightSensorManager getINSTANCE() {
		return INSTANCE;
	}
}
