package net.nrask.voidlockscreen.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.lockscreen_logic.AcDisplayLockscreen;
import net.nrask.voidlockscreen.lockscreen_logic.LockscreenUnlocker;
import net.nrask.voidlockscreen.services.StartLockscreenService;
import net.nrask.voidlockscreen.SRJService;

public class LockscreenActivity extends Activity implements View.OnTouchListener {
	private boolean checkingDrawPermission = false;

	public WindowManager windowManager;
	public RelativeLayout wrapperView;

	private LockscreenUnlocker unlocker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			showLockScreen();
		} catch (WindowManager.BadTokenException e) { // On Marshmallow and above this is an issue. We don't have the permission to draw over other applications even though it is declared in the manifest
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				checkDrawOverlayPermission();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkingDrawPermission && Settings.canDrawOverlays(this)) {
			recreate();
		} else {
			//Todo: Permission was denied. Show user that permission is absolutely needed.
		}
	}

	private void showLockScreen() {
		if (!SRJService.isServiceRunning(StartLockscreenService.class, getBaseContext())) {
			startService(new Intent(getBaseContext(), StartLockscreenService.class));
		}

		WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
						WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
						WindowManager.LayoutParams.FLAG_FULLSCREEN |
						WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
						WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
				PixelFormat.TRANSLUCENT);

		// Unfortunately the flags WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD are not working on some devices (Thanks Samsung!)
		// So to be sure the keyguard is disabled, we disable the keyguard using this deprecated method
		// Or you might want to specifically ask users to disable their system screen lock to get the best experience
		((KeyguardManager) getSystemService(KEYGUARD_SERVICE)).newKeyguardLock("IN").disableKeyguard();

		getWindow().setAttributes(localLayoutParams);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		wrapperView = new RelativeLayout(getBaseContext());
		wrapperView.setOnTouchListener(this);
		View.inflate(this, R.layout.lock_screen, wrapperView);

		windowManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));
		windowManager.addView(wrapperView, localLayoutParams);

		unlocker = new AcDisplayLockscreen(wrapperView, this);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void checkDrawOverlayPermission() {
		if (!Settings.canDrawOverlays(this)) {
			checkingDrawPermission = true;
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
			startActivity(intent);
		}
	}

	public void onUnlock(View view) {
		// Simple unlock by finishing activity and removing views
		windowManager.removeView(wrapperView);
		wrapperView.removeAllViews();

		finish();
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (unlocker == null) {
			return false;
		}

		Log.d(getClass().getSimpleName(), "TOUCH");

		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				unlocker.onUserTouchDown(motionEvent);
				break;
			case MotionEvent.ACTION_UP:
				unlocker.onUserTouchUp(motionEvent);
				break;
			case MotionEvent.ACTION_MOVE:
				unlocker.onUserTouchMove(motionEvent);
				break;
		}

		return true;
	}
}
