package net.nrask.voidlockscreen.unlockers;

import android.app.Activity;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.activities.LockscreenActivity;

/**
 * Created by Sebastian Rask on 20-12-2016.
 */

public abstract class LockscreenUnlocker extends MotionListener {

	public LockscreenUnlocker(RelativeLayout lockscreenContainer, Activity context) {
		super(lockscreenContainer, context);
	}

	protected void unlock() {
		if (context instanceof LockscreenActivity) {
			((LockscreenActivity) context).unlock();
		} else {
			context.finish();
		}
	}

	public void unlockNoTouch() {
		unlock();
	}
}
