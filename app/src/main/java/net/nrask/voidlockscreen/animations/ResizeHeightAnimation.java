package net.nrask.voidlockscreen.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Sebastian Rask on 21-12-2016.
 */

public class ResizeHeightAnimation extends Animation {
	private final int startHeight;
	private final int targetHeight;
	private View view;

	public ResizeHeightAnimation(View view, int targetHeight, int startHeight) {
		this.view = view;
		this.targetHeight = targetHeight;
		this.startHeight = startHeight;
	}

	public ResizeHeightAnimation(View view, int targetHeight) {
		this(view, targetHeight, view.getHeight());
	}

	public ResizeHeightAnimation(View view) {
		this(view, view.getMeasuredHeight());
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		view.getLayoutParams().height = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
		view.requestLayout();
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}
}

