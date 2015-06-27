package com.iac.smartmodeproject.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.managers.ModeManager;
import com.iac.smartmodeproject.managers.SettingManager;
import com.iac.smartmodeproject.services.MainService;
import com.iac.smartmodeproject.utils.DrivingMode;
import com.iac.smartmodeproject.utils.EventMode;
import com.iac.smartmodeproject.utils.PlaceMode;
import com.iac.smartmodeproject.utils.SleepingMode;

public class SettingsActivity extends Activity {
	private Fragment opened_fragment;
	private String TAG = "SettingActivity";
	private Intent service_intent;
	// private Switch switch_service;
	public static String SETTING_PREFERENCES_NAME = "SettingPreferences";
	private SettingManager setting_manger;
	private ModeManager modeManger;

	final Fragment mainFragment = new MainFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings_activity);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// starting service if not started
		service_intent = new Intent(this, MainService.class);

		if (!MainService.serviceStarted)
			startService(service_intent);

		final FragmentManager fragmentManager = getFragmentManager();
		final Fragment sleepingFragment = new SleepingModeFragment();
		final Fragment eventFragment = new EventModeFragment();
		final Fragment placeFragment = new PlaceModeFragment();
		final Fragment drivingFragment = new DrivingModeFragment();

		// initiating dialog
		final AlertDialog.Builder dgBuilder = new AlertDialog.Builder(
				SettingsActivity.this);

		final ImageButton sleepingModeBtn = (ImageButton) findViewById(R.id.sleeping_mode);
		sleepingModeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!sleepingFragment.isAdded()) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					if (opened_fragment != null) {
						transaction.setCustomAnimations(R.anim.left_enter,
								R.anim.right_exit);
					} else {
						transaction.setCustomAnimations(R.anim.right_enter,
								R.anim.left_exit);
					}
					transaction.replace(R.id.fragment_container,
							sleepingFragment).commit();
					opened_fragment = sleepingFragment;
				} else {
					if (((SleepingMode) modeManger.sleepingMode).isActive) {
						dgBuilder.setTitle(R.string.disable_sleeping_mode);
						dgBuilder
								.setMessage("Are you sure you want to disable sleeping mode ?");
						dgBuilder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((SleepingMode) modeManger.sleepingMode)
												.setEnabled(false);
										sleepingModeBtn
												.setSelected(!sleepingModeBtn
														.isSelected());
									}
								});
						dgBuilder.setNegativeButton("Cancel", null);
						dgBuilder.create().show();
					} else {
						((SleepingMode) modeManger.sleepingMode)
								.setEnabled(true);
						sleepingModeBtn.setSelected(!sleepingModeBtn
								.isSelected());
					}
				}
			}
		});

		final ImageButton eventModeBtn = (ImageButton) findViewById(R.id.event_mode);
		eventModeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!eventFragment.isAdded()) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					if (opened_fragment == sleepingFragment) {
						transaction.setCustomAnimations(R.anim.right_enter,
								R.anim.left_exit);
					} else {
						transaction.setCustomAnimations(R.anim.left_enter,
								R.anim.right_exit);
					}
					transaction.replace(R.id.fragment_container, eventFragment)
							.commit();
					opened_fragment = eventFragment;
				} else {
					if (((EventMode) modeManger.eventMode).isActive) {
						dgBuilder.setTitle(R.string.disable_event_mode);
						dgBuilder
								.setMessage("Are you sure you want to disable event mode ?");
						dgBuilder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((EventMode) modeManger.eventMode)
												.setEnabled(false);
										eventModeBtn.setSelected(!eventModeBtn
												.isSelected());
									}
								});
						dgBuilder.setNegativeButton("Cancel", null);
						dgBuilder.create().show();
					} else {
						((EventMode) modeManger.eventMode).setEnabled(true);
						eventModeBtn.setSelected(!eventModeBtn.isSelected());
					}
				}
			}
		});

		final ImageButton placeModeBtn = (ImageButton) findViewById(R.id.place_mode);
		placeModeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!placeFragment.isAdded()) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					if (opened_fragment == sleepingFragment
							|| opened_fragment == eventFragment) {
						transaction.setCustomAnimations(R.anim.right_enter,
								R.anim.left_exit);
					} else {
						transaction.setCustomAnimations(R.anim.left_enter,
								R.anim.right_exit);
					}
					transaction.replace(R.id.fragment_container, placeFragment)
							.commit();
					opened_fragment = placeFragment;
				} else {
					if (((PlaceMode) modeManger.placeMode).isActive) {
						dgBuilder.setTitle(R.string.disable_place_mode);
						dgBuilder
								.setMessage("Are you sure you want to disable place mode ?");
						dgBuilder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((PlaceMode) modeManger.placeMode)
												.setEnabled(false);
										placeModeBtn.setSelected(!placeModeBtn
												.isSelected());
									}
								});
						dgBuilder.setNegativeButton("Cancel", null);
						dgBuilder.create().show();
					} else {
						if (servicesAvailable()) {
							((PlaceMode) modeManger.placeMode).setEnabled(true);
							placeModeBtn.setSelected(!placeModeBtn.isSelected());
						}
					}
				}
			}
		});

		final ImageButton drivingModeBtn = (ImageButton) findViewById(R.id.driving_mode);
		drivingModeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!drivingFragment.isAdded()) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					if (opened_fragment != drivingFragment) {
						transaction.setCustomAnimations(R.anim.right_enter,
								R.anim.left_exit);
					} else {
						transaction.setCustomAnimations(R.anim.left_enter,
								R.anim.right_exit);
					}
					transaction.replace(R.id.fragment_container,
							drivingFragment).commit();
					opened_fragment = drivingFragment;
				} else {
					if (((DrivingMode) modeManger.drivingMode).isActive) {
						dgBuilder.setTitle(R.string.disable_driving_mode);
						dgBuilder
								.setMessage("Are you sure you want to disable driving mode ?");
						dgBuilder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((DrivingMode) modeManger.drivingMode)
												.setEnabled(false);
										drivingModeBtn
												.setSelected(!drivingModeBtn
														.isSelected());
									}
								});
						dgBuilder.setNegativeButton("Cancel", null);
						dgBuilder.create().show();
					} else {
						if (servicesAvailable()) {
							((DrivingMode) modeManger.drivingMode)
									.setEnabled(true);
							drivingModeBtn.setSelected(!drivingModeBtn
									.isSelected());
						}

					}
				}
			}
		});
		// initiating mode manager
		modeManger = ModeManager.getINSTANCE();

		// initiating setting manager
		setting_manger = SettingManager.getINSTANCE();
		setting_manger.context = getApplicationContext();
		setting_manger.init(SETTING_PREFERENCES_NAME);
		// load settings
		if (SettingManager.getINSTANCE().getSettingValue(
				SettingManager.SETTINGS_INITIATED, true) == true) {
			// opened for the first time
			SettingManager.getINSTANCE().modifySetting(
					SettingManager.SETTINGS_INITIATED, false);
			// initiating settings
			// set enabled modes
			setting_manger.modifySetting(
					SettingManager.ENABLED_DISABLED_DRIVING_MODE, true);
			setting_manger.modifySetting(
					SettingManager.ENABLED_DISABLED_SLEEPING_MODE, true);
			setting_manger.modifySetting(
					SettingManager.ENABLED_DISABLED_EVENT_MODE, true);
			setting_manger.modifySetting(
					SettingManager.ENABLED_DISABLED_PLACE_MODE, true);

			sleepingModeBtn.setSelected(true);
			eventModeBtn.setSelected(true);
			placeModeBtn.setSelected(true);
			drivingModeBtn.setSelected(true);

			// initiate options
			setting_manger.modifySetting(
					SettingManager.DRIVING_MODE_TTS_OPTION, true);

			setting_manger.modifySetting(
					SettingManager.SLEEPING_MODE_SMS_OPTION, false);
			setting_manger.modifySetting(SettingManager.PLACE_MODE_SMS_OPTION,
					false);
			setting_manger.modifySetting(SettingManager.EVENT_MODE_SMS_OPTION,
					false);
			setting_manger.modifySetting(
					SettingManager.DRIVING_MODE_SMS_OPTION, false);

			// default sms option msg
			setting_manger.modifySetting(SettingManager.SLEEPING_MODE_SMS_MSG,
					getString(R.string.sleeping_mode_sms));

			setting_manger.modifySetting(SettingManager.EVENT_MODE_SMS_MSG,
					getString(R.string.event_mode_sms));

			setting_manger.modifySetting(SettingManager.PLACE_MODE_SMS_MSG,
					getString(R.string.place_mode_sms));

			setting_manger.modifySetting(SettingManager.DRIVING_MODE_SMS_MSG,
					getString(R.string.driving_mode_sms));

		} else {
			sleepingModeBtn
					.setSelected(SettingManager
							.getINSTANCE()
							.getSettingValue(
									SettingManager.ENABLED_DISABLED_SLEEPING_MODE,
									true));
			eventModeBtn.setSelected(SettingManager.getINSTANCE()
					.getSettingValue(
							SettingManager.ENABLED_DISABLED_EVENT_MODE, true));
			placeModeBtn.setSelected(SettingManager.getINSTANCE()
					.getSettingValue(
							SettingManager.ENABLED_DISABLED_PLACE_MODE, true));
			drivingModeBtn
					.setSelected(SettingManager.getINSTANCE().getSettingValue(
							SettingManager.ENABLED_DISABLED_DRIVING_MODE, true));

		}

		fragmentManager.beginTransaction()
				.setCustomAnimations(R.anim.left_enter, R.anim.right_exit)
				.replace(R.id.fragment_container, mainFragment).commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private boolean servicesAvailable() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (resultCode == ConnectionResult.SUCCESS) {

			// Continue
			return true;

		} else { // Google Play services is not available!

			// Display an error dialog Dialog errorDialog =
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, this, 0);
			if (errorDialog != null) {
				errorDialog.show();
			}
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			break;
		case R.id.life_log:
			startActivity(new Intent(this, LifeLogActivity.class));
			break;

		case android.R.id.home:
			opened_fragment = null;
			getFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.left_enter, R.anim.right_exit)
					.replace(R.id.fragment_container, mainFragment).commit();

			break;
		}
		return true;
	}
}
