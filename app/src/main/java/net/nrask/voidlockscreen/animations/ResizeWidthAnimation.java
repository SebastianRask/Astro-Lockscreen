package net.nrask.voidlockscreen.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Sebastian Rask on 21-12-2016.
 */

public class ResizeWidthAnimation extends Animation {
	private final int startWidth;
	private final int targetWidth;
	private View view;

	public ResizeWidthAnimation(View view, int targetWidth, int startWidth) {
		this.view = view;
		this.targetWidth = targetWidth;
		this.startWidth = startWidth;
	}

	public ResizeWidthAnimation(View view, int targetWidth) {
		this(view, targetWidth, view.getWidth());
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		view.getLayoutParams().width = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
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
