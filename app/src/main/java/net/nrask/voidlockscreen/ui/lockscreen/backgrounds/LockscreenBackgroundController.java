package net.nrask.voidlockscreen.ui.lockscreen.backgrounds;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RelativeLayout;

import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_ERROR_RETRIEVING_NOTIFICATIONS;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_NOTIFICATIONS_RETRIEVED;
import static net.nrask.voidlockscreen.services.NotificationReaderService.ACTION_RETRIEVE_NOTIFICATIONS;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 23/12/16.
 */

public class LockscreenBackgroundController implements LifecycleObserver {

	public LockscreenBackgroundController(RelativeLayout container) {

	}

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	protected void resume() {
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	protected void pause() {
	}
}