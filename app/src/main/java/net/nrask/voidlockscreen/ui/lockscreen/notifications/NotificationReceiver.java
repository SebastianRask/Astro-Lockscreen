package net.nrask.voidlockscreen.ui.lockscreen.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import net.nrask.voidlockscreen.services.NotificationReaderService;

import java.util.ArrayList;
import java.util.List;

import static android.app.Notification.PRIORITY_LOW;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_ERROR_RETRIEVING_NOTIFICATIONS;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_NOTIFICATIONS_RETRIEVED;

/**
 * Created by Sebastian Rask Jepsen on 28/12/2017.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private NotificationReceiverListener mListener;

    public NotificationReceiver(NotificationReceiverListener listener) {
        this.mListener = listener;
    }

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

            mListener.OnSystemNotificationsReceived(notifications);

            Log.d(getClass().getSimpleName(), "Retrieved Notifications");
        } else if (intent.getAction().equals(ACTION_ERROR_RETRIEVING_NOTIFICATIONS)) {
            Log.d(getClass().getSimpleName(), "ERROR Retrieving Notifications");
        }

    }

    public interface NotificationReceiverListener {
        void OnSystemNotificationsReceived(List<StatusBarNotification> notifications);
    }
}
