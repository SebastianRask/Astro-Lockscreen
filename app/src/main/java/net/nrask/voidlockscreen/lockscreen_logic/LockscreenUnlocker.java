package net.nrask.voidlockscreen.lockscreen_logic;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.activities.LockscreenActivity;

/**
 * Created by Sebastian Rask on 20-12-2016.
 */

public abstract class LockscreenUnlocker {
	protected RelativeLayout lockscreenContainer;
	protected Activity context;

	public LockscreenUnlocker(RelativeLayout lockscreenContainer, Activity context) {
		this.lockscreenContainer = lockscreenContainer;
		this.context = context;
	}

	public abstract void onUserTouchDown(MotionEvent motionEvent);
	public abstract void onUserTouchUp(MotionEvent motionEvent);
	public abstract void onUserTouchMove(MotionEvent motionEvent);

	protected void setViewPosition(View view, int x, int y, boolean centerInPosition) {
		ViewGroup.LayoutParams oldParams = view.getLayoutParams();
		RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(0, 0);
		newParams.leftMargin = x;
		newParams.topMargin = y;
		newParams.width = oldParams.width;
		newParams.height = oldParams.height;

		if (centerInPosition) {
			newParams.leftMargin = newParams.leftMargin - newParams.width / 2;
			newParams.topMargin = newParams.topMargin - newParams.height / 2;
		}

		view.setLayoutParams(newParams);
	}

	protected void setViewSize(View view, int height, int width) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
		params.height = height;
		params.width = width;

		view.setLayoutParams(params);
	}

	protected void unlock() {
		if (context instanceof LockscreenActivity) {
			((LockscreenActivity) context).unlock();
		} else {
			context.finish();
		}
	}
}
