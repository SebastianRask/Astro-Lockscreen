package net.nrask.voidlockscreen.ui.lockscreen.unlockers;

import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.ui.LockscreenActivity;

/**
 * Created by Sebastian Rask on 20-12-2016.
 */

public abstract class LockscreenUnlocker extends MotionListener {
	private UnlockerCallback mCallback;

	public LockscreenUnlocker(RelativeLayout lockscreenContainer, UnlockerCallback callback) {
		super(lockscreenContainer);
		this.mCallback = callback;
	}

	protected void unlock() {
		if (mCallback == null) {
			return;
		}
		mCallback.onRequestUnlock();
	}

	public void unlockNoTouch() {
		unlock();
	}

	public interface UnlockerCallback {
		void onRequestUnlock();
	}
}
