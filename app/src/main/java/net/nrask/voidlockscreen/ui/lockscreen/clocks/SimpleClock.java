package net.nrask.voidlockscreen.ui.lockscreen.clocks;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.helpers.SRJHelper;

import java.util.Calendar;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 26/12/16.
 */

public class SimpleClock extends LockscreenClockController {

	public SimpleClock(RelativeLayout lockscreenContainer) {
		super(lockscreenContainer);

		View.inflate(mContext, R.layout.clock_simple, lockscreenContainer);

		final RelativeLayout mClockContainer = lockscreenContainer.findViewById(R.id.clock_container);
		TextClock mTextClock = lockscreenContainer.findViewById(R.id.text_clock);
		TextView mAmPmIndicator = lockscreenContainer.findViewById(R.id.text_am_pm_indicator);
		TextView mTextDate = lockscreenContainer.findViewById(R.id.text_date);

		setupClockViews(mTextClock, mAmPmIndicator, mClockContainer);
		setupTextDate(mTextDate, mContext);
	}

	private void setupClockViews(final TextView mainClock, TextView amPmIndicator, final View clockContainer) {
		//ToDo: Need align top of this view with textclock
		//mAmPmIndicator.setVisibility(mTextClock.is24HourModeEnabled() ? View.GONE : View.VISIBLE);

		mainClock.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mainClock.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				clockContainer.setTranslationY(SRJHelper.getScreenHeight(mContext)/5 - clockContainer.getHeight()/2);

			}
		});
	}

	private void setupTextDate(TextView dateText, Context context) {
		dateText.setText(
				context.getString(R.string.date_simple,
						getCalendarFieldFormatted(Calendar.DAY_OF_WEEK, true, false),
						getDayOfMonth(),
						getCalendarFieldFormatted(Calendar.MONTH, false, false)
				)
		);
	}
}
