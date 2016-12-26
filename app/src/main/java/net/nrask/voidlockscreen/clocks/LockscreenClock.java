package net.nrask.voidlockscreen.clocks;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.unlockers.MotionListener;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 26/12/16.
 */

public class LockscreenClock extends MotionListener {

	public LockscreenClock(RelativeLayout lockscreenContainer, Activity activity) {
		super(lockscreenContainer, activity);
	}

	@Override
	public void onUserTouchDown(MotionEvent motionEvent) {}

	@Override
	public void onUserTouchUp(MotionEvent motionEvent) {}

	@Override
	public void onUserTouchMove(MotionEvent motionEvent) {}

	@Override
	public void onDismissViews() {}
}
