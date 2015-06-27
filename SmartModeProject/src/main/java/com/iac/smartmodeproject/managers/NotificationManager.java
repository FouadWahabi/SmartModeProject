package com.iac.smartmodeproject.managers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.util.Log;

import com.iac.smartmodeproject.R;
import com.iac.smartmodeproject.ui.SettingsActivity;
import com.iac.smartmodeproject.utils.DrivingMode;
import com.iac.smartmodeproject.utils.EventMode;
import com.iac.smartmodeproject.utils.PlaceMode;
import com.iac.smartmodeproject.utils.SleepingMode;

public class NotificationManager {

	private NotificationManagerCompat notificationManager;
	private static NotificationManager INSATANCE;
	private Context context;
	private PendingIntent okBtnIntent;
	private PendingIntent dismissBtnIntent;
	private PendingIntent settingActicityPi;

	// constant variables
	public static final int SLEEPING_MODE_NOTIFICATION = 0;
	public static final int SLEEPING_MODE_NOTIFICATION_OK = 10;
	public static final int SLEEPING_MODE_NOTIFICATION_DISMISS = 20;
	public static final int EVENT_MODE_NOTIFICATION = 1;
	public static final int EVENT_MODE_NOTIFICATION_OK = 11;
	public static final int EVENT_MODE_NOTIFICATION_DISMISS = 21;
	public static final int PLACE_MODE_NOTIFICATION = 2;
	public static final int PLACE_MODE_NOTIFICATION_OK = 12;
	public static final int PLACE_MODE_NOTIFICATION_DISMISS = 22;
	public static final int DRIVING_MODE_NOTIFICATION = 3;
	public static final int DRIVING_MODE_NOTIFICATION_OK = 13;
	public static final int DRIVING_MODE_NOTIFICATION_DISMISS = 23;
	public static String GROUP_KEY = "SmartModeNotifications";
	private String NOTIFICATION_RECEIVER_ACTION = "tn.iac.intent.action.NOTIFICATION_RECEIVER";
	private String NOTIFICATION_SENDER = "SENDER";
	private String NOTIFICATION_ACTION = "ACTION";
	private int NOTIFICATION_OK_ACTION = 1;
	private int NOTIFICATION_DISMISS_ACTION = 2;

	// constructor
	private NotificationManager(Context context) {
		this.context = context;
		if (notificationManager == null) {
			notificationManager = NotificationManagerCompat.from(context);
		}

		settingActicityPi = PendingIntent.getActivity(context, 0, new Intent(
				context, SettingsActivity.class), Intent.FILL_IN_ACTION);
		BroadcastReceiver notificationReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(NOTIFICATION_RECEIVER_ACTION)) {
					if (intent.getIntExtra(NOTIFICATION_SENDER, -1) == SLEEPING_MODE_NOTIFICATION) {
						if (intent.getIntExtra(NOTIFICATION_ACTION, -1) == NOTIFICATION_OK_ACTION) {
							Log.i("NotificationManager",
									"Sleeping mode Ok btn clicked");
							notificationManager
									.cancel(SLEEPING_MODE_NOTIFICATION);
						} else {
							Log.i("NotificationManager",
									"Sleeping mode dismiss btn clicked");
							if (ModeManager.getINSTANCE().eventMode != null) {
								((SleepingMode) ModeManager.getINSTANCE().sleepingMode)
										.dispatch();
								notificationManager
										.cancel(SLEEPING_MODE_NOTIFICATION);
							}
						}
					}
					if (intent.getIntExtra(NOTIFICATION_SENDER, -1) == EVENT_MODE_NOTIFICATION) {
						if (intent.getIntExtra(NOTIFICATION_ACTION, -1) == NOTIFICATION_OK_ACTION) {
							Log.i("NotificationManager",
									"Event mode ok btn clicked");
							notificationManager.cancel(EVENT_MODE_NOTIFICATION);
						} else {
							Log.i("NotificationManager",
									"Event mode dismiss btn clicked");
							if (ModeManager.getINSTANCE().eventMode != null) {
								((EventMode) ModeManager.getINSTANCE().eventMode)
										.dispatch();
								notificationManager
										.cancel(EVENT_MODE_NOTIFICATION);
							}
						}
					}
					if (intent.getIntExtra(NOTIFICATION_SENDER, -1) == PLACE_MODE_NOTIFICATION) {
						if (intent.getIntExtra(NOTIFICATION_ACTION, -1) == NOTIFICATION_OK_ACTION) {
							notificationManager.cancel(PLACE_MODE_NOTIFICATION);
							Log.i("NotificationManager",
									"Place mode ok btn clicked");
						} else {
							Log.i("NotificationManager",
									"Place mode dismiss btn clicked");
							if (ModeManager.getINSTANCE().eventMode != null) {
								((PlaceMode) ModeManager.getINSTANCE().placeMode)
										.dispatch();
								notificationManager
										.cancel(PLACE_MODE_NOTIFICATION);
							}
						}
					}
					if (intent.getIntExtra(NOTIFICATION_SENDER, -1) == DRIVING_MODE_NOTIFICATION) {
						if (intent.getIntExtra(NOTIFICATION_ACTION, -1) == NOTIFICATION_OK_ACTION) {
							Log.i("NotificationManager",
									"Driving mode ok btn clicked");
							notificationManager
									.cancel(DRIVING_MODE_NOTIFICATION);
						} else {
							Log.i("NotificationManager",
									"Driving mode ok btn clicked");
							if (ModeManager.getINSTANCE().eventMode != null) {
								((DrivingMode) ModeManager.getINSTANCE().drivingMode)
										.dispatch();
								notificationManager
										.cancel(DRIVING_MODE_NOTIFICATION);
							}
						}
					}
				}
			}
		};
		context.registerReceiver(notificationReceiver, new IntentFilter(
				NOTIFICATION_RECEIVER_ACTION));

	}

	public void createNotification(int modeNotification) {

		switch (modeNotification) {
		case SLEEPING_MODE_NOTIFICATION:
			notificationManager.notify(SLEEPING_MODE_NOTIFICATION,
					buildSleepingModeNotification());

			break;

		case EVENT_MODE_NOTIFICATION:
			notificationManager.notify(EVENT_MODE_NOTIFICATION,
					buildEventModeNotification());

			break;

		case PLACE_MODE_NOTIFICATION:
			notificationManager.notify(PLACE_MODE_NOTIFICATION,
					buildPlaceModeNotification());

			break;

		case DRIVING_MODE_NOTIFICATION:
			notificationManager.notify(DRIVING_MODE_NOTIFICATION,
					buildDrivingModeNotification());

			break;

		default:
			break;
		}
	}

	private Notification buildDrivingModeNotification() {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				context)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				// add group stack
				.setGroup(GROUP_KEY)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.ic_launcher))
				.setContentIntent(settingActicityPi)
				// add notification summary
				.setGroupSummary(true).setAutoCancel(false).setOngoing(true);
		// adding pages to notification ( addPage )
		// setting custom background ( setBackground )
		notificationBuilder
				.setContentTitle("Driving mode applied")
				.setSmallIcon(R.drawable.ic_driving_mode)
				.setContentText("Driving mode applied")
				.extend(new NotificationCompat.WearableExtender()
						.setHintShowBackgroundOnly(false)
						// add content action to make part of view clickable
						.setContentAction(0)
						.setBackground(
								BitmapFactory.decodeResource(
										context.getResources(),
										R.drawable.ic_launcher))
				// adding remote input for reply action
				// .addAction(
				// new NotificationCompat.Action.Builder(0,
				// "Reply", null).addRemoteInput(
				// new RemoteInput.Builder("quick_reply")
				// .setLabel("Quick select mode")
				// .build()).build())

				);
		// setting big style
		notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
				.bigText(Html.fromHtml(context
						.getString(R.string.driving_mode_desc))));
		// add action to notification
		okBtnIntent = PendingIntent.getBroadcast(
				context,
				DRIVING_MODE_NOTIFICATION_OK,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, DRIVING_MODE_NOTIFICATION)
						.putExtra(NOTIFICATION_ACTION, NOTIFICATION_OK_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		dismissBtnIntent = PendingIntent.getBroadcast(
				context,
				DRIVING_MODE_NOTIFICATION_DISMISS,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, DRIVING_MODE_NOTIFICATION)
						.putExtra(NOTIFICATION_ACTION,
								NOTIFICATION_DISMISS_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.addAction(R.drawable.btn_ok, "Ok", okBtnIntent);
		notificationBuilder.addAction(R.drawable.btn_dismiss, "Dismiss",
				dismissBtnIntent);

		notificationBuilder.setPriority(Notification.PRIORITY_MAX);
		return notificationBuilder.build();
	}

	private Notification buildPlaceModeNotification() {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				context)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				// add group stack
				.setGroup(GROUP_KEY)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.ic_launcher))
				.setContentIntent(settingActicityPi)
				// add notification summary
				.setGroupSummary(true).setAutoCancel(false).setOngoing(true);
		// adding pages to notification ( addPage )
		// setting custom background ( setBackground )
		notificationBuilder
				.setContentTitle("Place mode applied")
				.setSmallIcon(R.drawable.ic_place_mode)
				.setContentText("Place mode applied")
				.extend(new NotificationCompat.WearableExtender()
						.setHintShowBackgroundOnly(false)
						// add content action to make part of view clickable
						.setContentAction(0)
						.setBackground(
								BitmapFactory.decodeResource(
										context.getResources(),
										R.drawable.ic_launcher))
				// adding remote input for reply action
				// .addAction(
				//
				// new NotificationCompat.Action.Builder(0,
				// "Reply", null).addRemoteInput(
				// new RemoteInput.Builder("quick_reply")
				// .setLabel("Quick select mode")
				// .build()).build())

				);
		// setting big style
		notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
				.bigText(Html.fromHtml(context
						.getString(R.string.place_mode_desc))));
		// add action to notification
		dismissBtnIntent = PendingIntent.getBroadcast(
				context,
				PLACE_MODE_NOTIFICATION_DISMISS,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, PLACE_MODE_NOTIFICATION).putExtra(
						NOTIFICATION_ACTION, NOTIFICATION_DISMISS_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		okBtnIntent = PendingIntent.getBroadcast(
				context,
				PLACE_MODE_NOTIFICATION_OK,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, PLACE_MODE_NOTIFICATION).putExtra(
						NOTIFICATION_ACTION, NOTIFICATION_OK_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.addAction(R.drawable.btn_ok, "Ok", okBtnIntent);
		notificationBuilder.addAction(R.drawable.btn_dismiss, "Dismiss",
				dismissBtnIntent);

		notificationBuilder.setPriority(Notification.PRIORITY_MAX);
		return notificationBuilder.build();
	}

	private Notification buildEventModeNotification() {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				context)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				// add group stack
				.setGroup(GROUP_KEY)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.ic_launcher))
				.setContentIntent(settingActicityPi)
				// add notification summary
				.setGroupSummary(true).setAutoCancel(false).setOngoing(true);
		// adding pages to notification ( addPage )
		// setting custom background ( setBackground )
		notificationBuilder
				.setContentTitle("Event mode applied")
				.setSmallIcon(R.drawable.ic_event_mode)
				.setContentText("Event mode applied")
				.extend(new NotificationCompat.WearableExtender()
						.setHintShowBackgroundOnly(false)
						// add content action to make part of view clickable
						.setContentAction(0)
						.setBackground(
								BitmapFactory.decodeResource(
										context.getResources(),
										R.drawable.ic_launcher))
				// adding remote input for reply action
				//
				// .addAction(
				// new NotificationCompat.Action.Builder(0,
				// "Reply", null).addRemoteInput(
				// new RemoteInput.Builder("quick_reply")
				// .setLabel("Quick select mode")
				// .build()).build())
				//
				);
		// setting big style
		notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
				.bigText(Html.fromHtml(context
						.getString(R.string.event_mode_desc))));
		// add action to notification
		okBtnIntent = PendingIntent.getBroadcast(
				context,
				EVENT_MODE_NOTIFICATION_OK,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, EVENT_MODE_NOTIFICATION).putExtra(
						NOTIFICATION_ACTION, NOTIFICATION_OK_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		dismissBtnIntent = PendingIntent.getBroadcast(
				context,
				EVENT_MODE_NOTIFICATION_DISMISS,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, EVENT_MODE_NOTIFICATION).putExtra(
						NOTIFICATION_ACTION, NOTIFICATION_DISMISS_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.addAction(R.drawable.btn_ok, "Ok", okBtnIntent);
		notificationBuilder.addAction(R.drawable.btn_dismiss, "Dismiss",
				dismissBtnIntent);

		notificationBuilder.setPriority(Notification.PRIORITY_MAX);
		return notificationBuilder.build();
	}

	private Notification buildSleepingModeNotification() {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				context)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				// add group stack
				.setGroup(GROUP_KEY)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.ic_launcher))
				.setContentIntent(settingActicityPi)
				// add notification summary
				.setGroupSummary(true).setAutoCancel(false).setOngoing(true);
		// adding pages to notification ( addPage )
		// setting custom background ( setBackground )
		notificationBuilder
				.setContentTitle("Sleeping mode applied")
				.setSmallIcon(R.drawable.ic_sleeping_mode)
				.setContentText("Sleeping mode applied")
				.extend(new NotificationCompat.WearableExtender()
						.setHintShowBackgroundOnly(false)
						// add content action to make part of view clickable
						.setContentAction(0)
						.setBackground(
								BitmapFactory.decodeResource(
										context.getResources(),
										R.drawable.ic_launcher))
				// adding remote input for reply action
				//
				// .addAction(
				// new NotificationCompat.Action.Builder(0,
				// "Reply", null).addRemoteInput(
				// new RemoteInput.Builder("quick_reply")
				// .setLabel("Quick select mode")
				// .build()).build())
				//
				);
		// setting big style
		notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
				.bigText(Html.fromHtml(context
						.getString(R.string.sleeping_mode_desc))));
		// add action to notification
		okBtnIntent = PendingIntent.getBroadcast(
				context,
				SLEEPING_MODE_NOTIFICATION_OK,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, SLEEPING_MODE_NOTIFICATION)
						.putExtra(NOTIFICATION_ACTION, NOTIFICATION_OK_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		dismissBtnIntent = PendingIntent.getBroadcast(
				context,
				SLEEPING_MODE_NOTIFICATION_DISMISS,
				new Intent(NOTIFICATION_RECEIVER_ACTION).putExtra(
						NOTIFICATION_SENDER, SLEEPING_MODE_NOTIFICATION)
						.putExtra(NOTIFICATION_ACTION,
								NOTIFICATION_DISMISS_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.addAction(R.drawable.btn_ok, "Ok", okBtnIntent);
		notificationBuilder.addAction(R.drawable.btn_dismiss, "Dismiss",
				dismissBtnIntent);

		notificationBuilder.setPriority(Notification.PRIORITY_MAX);
		return notificationBuilder.build();
	}

	public static NotificationManager getINSATANCE(Context context) {
		return INSATANCE == null ? INSATANCE = new NotificationManager(context)
				: INSATANCE;
	}

}
