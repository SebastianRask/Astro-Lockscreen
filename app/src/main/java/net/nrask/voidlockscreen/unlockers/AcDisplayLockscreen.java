package net.nrask.voidlockscreen.unlockers;

import android.animation.Animator;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.ui.LockscreenActivity;
import net.nrask.voidlockscreen.helpers.SRJHelper;

/**
 * Created by Sebastian Rask on 20-12-2016.
 */

public class AcDisplayLockscreen extends LockscreenUnlocker {
	private View unlockView, imgLock;
	private ViewPropertyAnimator scaleAnimator;
	private FrameLayout dimView;

	private int originalX, originalY;
	private int unlockDistance;
	private float maxAlpha = 0.6f;
	private boolean hasVibrated = false;
	private boolean unlockOnUp = false;

	public AcDisplayLockscreen(RelativeLayout lockscreenContainer, LockscreenActivity context) {
		super(lockscreenContainer, context);
		unlockDistance = context.getResources().getDimensionPixelSize(R.dimen.lockscreen_ac_display_unlock_distance);

		dimView = new FrameLayout(context);
		dimView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
		));
		dimView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
		dimView.setAlpha(0f);
		lockscreenContainer.addView(dimView);

		View.inflate(context, R.layout.unlocker_ac_display, lockscreenContainer);
		imgLock = lockscreenContainer.findViewById(R.id.img_unlock_lock);
		imgLock.setScrollX(0);
		imgLock.setScrollY(0);
		imgLock.setAlpha(0f);

		unlockView = lockscreenContainer.findViewById(R.id.circle_unlock_container);
		unlockView.setScaleX(0f);
		unlockView.setScaleY(0f);
	}

	@Override
	public void onUserTouchDown(MotionEvent motionEvent) {
		originalX = (int) motionEvent.getRawX();
		originalY = (int) motionEvent.getRawY();

		setViewPosition(imgLock, originalX, originalY, true);
		setViewPosition(unlockView, originalX, originalY, true);
	}

	@Override
	public void onUserTouchUp(MotionEvent motionEvent) {
		if (unlockOnUp) {
			unlock();
		} else {
			onDismissViews();
		}
		hasVibrated = false;

	}

	@Override
	public void onUserTouchMove(MotionEvent motionEvent) {
		float distance = Math.max(
				Math.abs(motionEvent.getRawX() - originalX),
				Math.abs(motionEvent.getRawY() - originalY)
		);

		unlockOnUp = distance > unlockDistance;
		if (unlockOnUp && !hasVibrated) {
			SRJHelper.vibrate(20, activity);
			hasVibrated = true;
		}

		float scale = distance/(activity.getResources().getDimensionPixelSize(R.dimen.lockscreen_ac_display_unlock_distance_max));

		dimView.animate().alpha(scale/maxAlpha).setDuration(0).start();

		Interpolator customInterpolator = PathInterpolatorCompat.create(0.080f, 1.80f, 0.590f, 1.70f);
		scale = customInterpolator.getInterpolation(scale);
		scaleAnimator = unlockView.animate().scaleX(scale).scaleY(scale).setDuration(0);
		scaleAnimator.start();

		float imgLockScale = scale/2;
		if (imgLockScale > 0.35f) {
			imgLockScale = 0.35f;
		}

		imgLock.animate().alpha(scale).scaleX(imgLockScale).scaleY(imgLockScale).setDuration(0).start();

	}

	@Override
	public void onDismissViews() {
		if (scaleAnimator != null) {
			scaleAnimator.scaleY(0f).scaleX(0f).setDuration(450).setInterpolator(new AccelerateInterpolator()).start();
		}
		imgLock.animate().scaleY(0f).scaleX(0f).setDuration(350).setInterpolator(new AccelerateInterpolator()).start();
		dimView.animate().alpha(0f).setDuration(450).setInterpolator(new DecelerateInterpolator()).start();
	}

	@Override
	protected void unlock() {
		int size = (int) (SRJHelper.getScreenHeight(activity) * 2);
		int duration = 440;
		int alphaDuration = 340;

		float currentScale = unlockView.getScaleX();
		float targetScale = size/unlockDistance;
		dimView.animate().alpha(1f).setDuration(alphaDuration).setInterpolator(new AccelerateInterpolator()).start();
		unlockView.animate().scaleY(targetScale).scaleX(targetScale).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				AcDisplayLockscreen.super.unlock();
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				AcDisplayLockscreen.super.unlock();
			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		}).start();
	}
}
