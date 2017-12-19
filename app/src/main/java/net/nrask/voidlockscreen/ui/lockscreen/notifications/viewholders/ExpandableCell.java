package net.nrask.voidlockscreen.ui.lockscreen.notifications.viewholders;

import android.view.View;

/**
 * Created by Sebastian Rask on 11-02-2017.
 */

public interface ExpandableCell {
	int getCollapsedHeight();
	int getExpandedHeight();
	View getExpandableView();
}
