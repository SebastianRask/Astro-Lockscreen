package net.nrask.voidlockscreen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 29/12/16.
 */

public class InterceptingRecyclerView extends RecyclerView {
	public InterceptingRecyclerView(Context context) {
		super(context);
	}

	public InterceptingRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public InterceptingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		return false;
	}
}
