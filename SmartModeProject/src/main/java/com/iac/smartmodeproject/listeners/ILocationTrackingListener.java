package com.iac.smartmodeproject.listeners;

import android.location.Location;

public interface ILocationTrackingListener {

	public void onLocationChanged(Location location);

	public void onSpeedChanged(double speed);

}
