package net.nrask.voidlockscreen.lockscreen_logic;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.animations.ResizeHeightAnimation;
import net.nrask.voidlockscreen.animations.ResizeWidthAnimation;
import net.nrask.voidlockscreen.helpers.SRJHelper;

/**
 * Created by Sebastian Rask on 20-12-2016.
 */

public class AcDisplayLockscreen extends LockscreenUnlocker {
	private RelativeLayout lockscreenContainer;
	private View unlockView, imgLock;
	private ViewPropertyAnimator scaleAnimator, dimAnimator;
	private FrameLayout dimView;

	private int originalX, originalY;
	private int unlockDistance;
	private float maxAlpha = 0.6f;
	private boolean hasVibrated = false;
	private boolean unlockOnUp = false;

	public AcDisplayLockscreen(RelativeLayout lockscreenContainer, Activity context) {
		super(lockscreenContainer, context);
		this.lockscreenContainer = lockscreenContainer;
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
			scaleAnimator.scaleY(0f).scaleX(0f).setDuration(450).setInterpolator(new AccelerateInterpolator()).start();
			dimAnimator.alpha(0f).setInterpolator(new DecelerateInterpolator()).start();
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
			SRJHelper.vibrate(20, context);
			hasVibrated = true;
		}

		float scale = distance/(context.getResources().getDimensionPixelSize(R.dimen.lockscreen_ac_display_unlock_distance_max));

		dimAnimator = dimView.animate().alpha(scale/maxAlpha).setDuration(0);
		dimAnimator.start();

		Interpolator customInterpolator = PathInterpolatorCompat.create(0.080f, 1.80f, 0.590f, 1.70f);
		scale = customInterpolator.getInterpolation(scale);

		scaleAnimator = unlockView.animate().scaleX(scale).scaleY(scale).setDuration(0);
		scaleAnimator.start();
	}

	@Override
	protected void unlock() {
		int size = (int) (SRJHelper.getScreenHeight(context) * 2);
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
