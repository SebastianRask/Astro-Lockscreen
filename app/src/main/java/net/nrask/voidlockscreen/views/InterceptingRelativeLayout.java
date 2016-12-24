package net.nrask.voidlockscreen.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 24/12/16.
 */

public class InterceptingRelativeLayout extends RelativeLayout {
	public InterceptingRelativeLayout(Context context) {
		super(context);
	}

	public InterceptingRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterceptingRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public InterceptingRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
}
