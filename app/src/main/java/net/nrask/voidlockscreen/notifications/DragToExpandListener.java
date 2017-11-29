package net.nrask.voidlockscreen.notifications;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import net.nrask.voidlockscreen.notifications.viewholders.ExpandableCell;

/**
 * Created by Sebastian Rask on 11-02-2017.
 */

public class DragToExpandListener implements View.OnTouchListener {
	private ExpandableCell mExpandableCell;
	private int mCurrentDragDistance;
	private float mStartX, mStartY;

	public DragToExpandListener(ExpandableCell mExpandableCell) {
		this.mExpandableCell = mExpandableCell;
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		View viewToExpand = mExpandableCell.getExpandableView();

		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mStartX = motionEvent.getX();
				mStartY = motionEvent.getY();
				break;

			case MotionEvent.ACTION_UP:
				mCurrentDragDistance = 0;
				viewToExpand.performClick();
				break;

			case MotionEvent.ACTION_MOVE:
				final float x = motionEvent.getX();
				final float y = motionEvent.getY();

				// Calculate the distance moved
				final float dx = x - mStartX;
				final float dy = y - mStartY;

				mCurrentDragDistance += dy;

				Log.d(getClass().getSimpleName(), "Height Before: " + viewToExpand.getLayoutParams().height);
				viewToExpand.getLayoutParams().height += dy;
				Log.d(getClass().getSimpleName(), "Height After: " + viewToExpand.getLayoutParams().height);
				break;
		}
		return false;
	}
}
