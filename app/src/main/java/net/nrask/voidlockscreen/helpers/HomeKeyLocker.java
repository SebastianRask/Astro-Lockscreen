package net.nrask.voidlockscreen.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.firebase.crash.FirebaseCrash;

import net.nrask.voidlockscreen.R;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

public class HomeKeyLocker {
    private OverlayDialog mOverlayDialog;

    public void lock(Activity activity) {
        try {
            if (mOverlayDialog == null) {
                mOverlayDialog = new OverlayDialog(activity);
                mOverlayDialog.show();
            }
        } catch (WindowManager.BadTokenException e) {
            FirebaseCrash.report(e);
        }

    }

    public void unlock() {
        if (mOverlayDialog != null) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
        }
    }

    private static class OverlayDialog extends AlertDialog {

        public OverlayDialog(Activity activity) {
            super(activity, R.style.OverlayDialog);
            if (getWindow() == null) {
                return;
            }

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.type = getWindowType();
            params.dimAmount = 0.0F; // transparent
            params.width = 0;
            params.height = 0;
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            getWindow().setFlags(FLAG_SHOW_WHEN_LOCKED | FLAG_NOT_TOUCH_MODAL, 0xffffff);
            setOwnerActivity(activity);
            setCancelable(false);
        }

        @Override
        public final boolean dispatchTouchEvent(@NonNull MotionEvent motionevent) {
            return true;
        }

        @Override
        protected final void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FrameLayout framelayout = new FrameLayout(getContext());
            framelayout.setBackgroundColor(0);
            setContentView(framelayout);
        }

        private int getWindowType() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? TYPE_APPLICATION_OVERLAY : TYPE_SYSTEM_ERROR;
        }
    }
}
