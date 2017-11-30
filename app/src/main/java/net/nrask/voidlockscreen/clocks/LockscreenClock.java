package net.nrask.voidlockscreen.clocks;

import android.view.MotionEvent;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.ui.LockscreenActivity;
import net.nrask.voidlockscreen.unlockers.MotionListener;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 26/12/16.
 */

public abstract class LockscreenClock extends MotionListener {

	public LockscreenClock(RelativeLayout lockscreenContainer, LockscreenActivity activity) {
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

	int getDayOfMonth() {
		Calendar mCalendar = Calendar.getInstance();
		return mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	String getCalendarFieldFormatted(int field, boolean shorten, boolean capitalize) {
		Calendar mCalendar = Calendar.getInstance();
		String result = mCalendar.getDisplayName(field, shorten ? Calendar.SHORT : Calendar.LONG, Locale.getDefault());
		return capitalize ? result.toUpperCase() : result;
	}
}
