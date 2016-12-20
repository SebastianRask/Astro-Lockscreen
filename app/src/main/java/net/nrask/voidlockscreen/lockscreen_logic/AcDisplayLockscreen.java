package net.nrask.voidlockscreen.lockscreen_logic;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.R;

/**
 * Created by Sebastian Rask on 20-12-2016.
 */

public class AcDisplayLockscreen extends LockscreenUnlocker {
	private MotionEvent touchDownPoint;
	private View unlockView;
	private ViewPropertyAnimator animator;
	private FrameLayout dimView;

	private float originalX, originalY;
	private int unlockDistance;
	private float maxAlpha = 0.6f;

	public AcDisplayLockscreen(RelativeLayout lockscreenContainer, Context context) {
		super(lockscreenContainer, context);
		dimView = new FrameLayout(context);
		dimView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
		));
		dimView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
		dimView.setAlpha(0f);
		lockscreenContainer.addView(dimView);

		unlockDistance = context.getResources().getDimensionPixelSize(R.dimen.lockscreen_ac_display_unlock_distance);
		unlockView = lockscreenContainer.findViewById(R.id.circle_unlock_container);
		unlockView.setScaleX(0f);
		unlockView.setScaleY(0f);
	}

	@Override
	public void onUserTouchDown(MotionEvent motionEvent) {
		touchDownPoint = motionEvent;
		originalX = motionEvent.getRawX();
		originalY = motionEvent.getRawY();

		setViewPosition(unlockView, (int) originalX, (int) originalY, true);
	}

	@Override
	public void onUserTouchUp(MotionEvent motionEvent) {
		animator.scaleY(0f).scaleX(0f).setDuration(450).setInterpolator(new AccelerateInterpolator()).start();
	}

	@Override
	public void onUserTouchMove(MotionEvent motionEvent) {
		float distance = Math.max(
				Math.abs(motionEvent.getRawX() - originalX),
				Math.abs(motionEvent.getRawY() - originalY)
		);

		float scale = distance/unlockDistance;
		animator = unlockView.animate().scaleX(scale).scaleY(scale).setDuration(0);
		animator.start();
	}
}
