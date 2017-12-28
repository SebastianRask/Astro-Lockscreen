package net.nrask.voidlockscreen.ui.lockscreen.notifications;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.ui.LockscreenActivity;
import net.nrask.voidlockscreen.ui.lockscreen.unlockers.MotionListener;

import java.util.ArrayList;
import java.util.List;

import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_ERROR_RETRIEVING_NOTIFICATIONS;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_NOTIFICATIONS_RETRIEVED;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_RETRIEVE_NOTIFICATIONS;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 27/12/16.
 */

public abstract class LockscreenNotificationsController extends MotionListener
		implements NotificationReceiver.NotificationReceiverListener, LifecycleObserver {

	private NotificationReceiver mNotificationsListener;
	List<StatusBarNotification> mNotifications;
	RecyclerView.Adapter mAdapter;

	public LockscreenNotificationsController(RelativeLayout lockscreenContainer) {
		super(lockscreenContainer);

		mNotificationsListener = new NotificationReceiver(this);
	}

	@Override
	public void onUserTouchDown(MotionEvent motionEvent) {}

	@Override
	public void onUserTouchUp(MotionEvent motionEvent) {}

	@Override
	public void onUserTouchMove(MotionEvent motionEvent) {}

	@Override
	public void onDismissViews() {}

	@Override
	public void OnSystemNotificationsReceived(List<StatusBarNotification> notifications) {
		if (mAdapter == null) {
			return;
		}

		mNotifications = notifications;
		mAdapter.notifyDataSetChanged();
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	protected void create() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_NOTIFICATIONS_RETRIEVED);
		intentFilter.addAction(ACTION_ERROR_RETRIEVING_NOTIFICATIONS);
		mContext.registerReceiver(mNotificationsListener, intentFilter);

		// Request notifications from system
		Intent retrieveNotifications = new Intent(ACTION_RETRIEVE_NOTIFICATIONS);
		mContext.sendBroadcast(retrieveNotifications);
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	protected void destroy() {
		mContext.unregisterReceiver(mNotificationsListener);
	}
}
