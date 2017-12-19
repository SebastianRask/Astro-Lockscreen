package net.nrask.voidlockscreen.ui.lockscreen.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.ui.LockscreenActivity;
import net.nrask.voidlockscreen.services.NotificationReaderService;
import net.nrask.voidlockscreen.ui.lockscreen.unlockers.MotionListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Notification.PRIORITY_LOW;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_ERROR_RETRIEVING_NOTIFICATIONS;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_NOTIFICATIONS_RETRIEVED;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_RETRIEVE_NOTIFICATIONS;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 27/12/16.
 */

public abstract class LockscreenNotificationsView extends MotionListener {
	private NotificationListener mNotificationsListener;
	protected ArrayList<StatusBarNotification> mNotifications;
	protected RecyclerView.Adapter mAdapter;

	public LockscreenNotificationsView(RelativeLayout lockscreenContainer, LockscreenActivity activity) {
		super(lockscreenContainer, activity);

		mNotificationsListener = new NotificationListener();
		IntentFilter intentFilter = new IntentFilter(ACTION_NOTIFICATIONS_RETRIEVED);
		intentFilter.addAction(ACTION_ERROR_RETRIEVING_NOTIFICATIONS);
		activity.registerReceiver(mNotificationsListener, intentFilter);

		Log.d(getClass().getSimpleName(), "Broadcasting retrieve notifications");
		Intent retrieveNotifications = new Intent(ACTION_RETRIEVE_NOTIFICATIONS);
		activity.sendBroadcast(retrieveNotifications);
	}


	public void activityDestroyed() {
		activity.unregisterReceiver(mNotificationsListener);
	}

	@Override
	public void onUserTouchDown(MotionEvent motionEvent) {}

	@Override
	public void onUserTouchUp(MotionEvent motionEvent) {}

	@Override
	public void onUserTouchMove(MotionEvent motionEvent) {}

	@Override
	public void onDismissViews() {}

	public class NotificationListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(getClass().getSimpleName(), "Retrieved " + intent.getAction());
			if (intent.getAction().equals(ACTION_NOTIFICATIONS_RETRIEVED)) {
				List<StatusBarNotification> notifications = new ArrayList<>();
				for (StatusBarNotification sbn : NotificationReaderService.mCurrentNotifications) {
					if (sbn.getNotification().priority >= PRIORITY_LOW) {
						notifications.add(sbn);
					}
				}

				mNotifications = new ArrayList<>(notifications);
				mAdapter.notifyDataSetChanged();

				Log.d(getClass().getSimpleName(), "Retrieved Notifications");
			} else if (intent.getAction().equals(ACTION_ERROR_RETRIEVING_NOTIFICATIONS)) {
				Log.d(getClass().getSimpleName(), "ERROR Retrieving Notifications");
			}

		}
	}
}
