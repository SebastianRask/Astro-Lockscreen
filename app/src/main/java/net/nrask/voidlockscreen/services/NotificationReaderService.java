package net.nrask.voidlockscreen.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 27/12/16.
 */

public class NotificationReaderService extends NotificationListenerService {
	public static String ACTION_RETRIEVE_NOTIFICATIONS = "net.nrask.voidlockscreen.RETRIEVE_NOTIFICATIONS";
	public static String ACTION_NOTIFICATIONS_RETRIEVED = "net.nrask.voidlockscreen.RETRIEVED_NOTIFICATIONS";
	public static String ACTION_ERROR_RETRIEVING_NOTIFICATIONS = "net.nrask.voidlockscreen.RETRIEVE_NOTIFICATIONS_ERROR";

	public static List<StatusBarNotification> mCurrentNotifications = new ArrayList<>();

	private NLServiceReceiver mNLServiceReceiver;

	@Override
	public void onCreate() {
		super.onCreate();

		mNLServiceReceiver = new NLServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_RETRIEVE_NOTIFICATIONS);
		registerReceiver(mNLServiceReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNLServiceReceiver);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		super.onNotificationPosted(sbn);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		super.onNotificationRemoved(sbn);
	}


	public class NLServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(getClass().getSimpleName(), "RECEIVED " + intent.getAction());
			if(intent.getAction().equals(ACTION_RETRIEVE_NOTIFICATIONS)){
				try {
					Log.d(getClass().getSimpleName(), "Broadcasting notifications");

					StatusBarNotification notifications[] = getActiveNotifications();
					mCurrentNotifications = new ArrayList<>(Arrays.asList(notifications));

					Intent notificationsRetrieved = new  Intent(ACTION_NOTIFICATIONS_RETRIEVED);
					sendBroadcast(notificationsRetrieved);

				} catch (Exception e) {
					e.printStackTrace();

					Log.d(getClass().getSimpleName(), "Error retrieving notifications");

					Intent errorIntent = new  Intent(ACTION_ERROR_RETRIEVING_NOTIFICATIONS);
					sendBroadcast(errorIntent);
				}
			}
		}
	}
}
