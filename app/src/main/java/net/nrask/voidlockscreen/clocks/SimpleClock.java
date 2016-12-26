package net.nrask.voidlockscreen.clocks;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.helpers.SRJHelper;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 26/12/16.
 */

public class SimpleClock extends LockscreenClock {

	public SimpleClock(RelativeLayout lockscreenContainer, final Activity activity) {
		super(lockscreenContainer, activity);

		View.inflate(activity, R.layout.clock_simple, lockscreenContainer);

		final RelativeLayout mClockContainer = (RelativeLayout) lockscreenContainer.findViewById(R.id.clock_container);
		TextClock mTextClock = (TextClock) lockscreenContainer.findViewById(R.id.text_clock);
		TextView mAmPmIndicator = (TextView) lockscreenContainer.findViewById(R.id.text_am_pm_indicator);

		//ToDo: find way to align top of this view with textclock
		//mAmPmIndicator.setVisibility(mTextClock.is24HourModeEnabled() ? View.GONE : View.VISIBLE);

		mClockContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mClockContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				mClockContainer.setTranslationY(SRJHelper.getScreenHeight(activity)/5 - mClockContainer.getHeight()/2);

			}
		});
	}
}
