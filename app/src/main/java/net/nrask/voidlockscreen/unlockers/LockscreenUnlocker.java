package net.nrask.voidlockscreen.unlockers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.nrask.voidlockscreen.activities.LockscreenActivity;
import net.nrask.voidlockscreen.helpers.SRJHelper;

/**
 * Created by Sebastian Rask on 20-12-2016.
 */

public abstract class LockscreenUnlocker extends LockscreenMotionListener {

	public LockscreenUnlocker(RelativeLayout lockscreenContainer, Activity context) {
		super(lockscreenContainer, context);
	}

	protected void unlock() {
		if (context instanceof LockscreenActivity) {
			((LockscreenActivity) context).unlock();
		} else {
			context.finish();
		}
	}

	public void unlockNoTouch() {
		unlock();
	}
}
