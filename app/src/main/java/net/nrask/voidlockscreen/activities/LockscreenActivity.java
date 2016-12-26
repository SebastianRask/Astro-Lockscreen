package net.nrask.voidlockscreen.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.backgrounds.LockscreenBackground;
import net.nrask.voidlockscreen.backgrounds.SensorBackground;
import net.nrask.voidlockscreen.clocks.LockscreenClock;
import net.nrask.voidlockscreen.clocks.SimpleClock;
import net.nrask.voidlockscreen.helpers.UtilHelper;
import net.nrask.voidlockscreen.unlockers.AcDisplayLockscreen;
import net.nrask.voidlockscreen.unlockers.LockscreenUnlocker;
import net.nrask.voidlockscreen.services.StartLockscreenService;
import net.nrask.voidlockscreen.SRJService;
import net.nrask.voidlockscreen.views.InterceptingRelativeLayout;

public class LockscreenActivity extends Activity implements View.OnTouchListener {
	private boolean checkingDrawPermission = false;

	public WindowManager windowManager;
	public RelativeLayout wrapperView;

	private LockscreenUnlocker unlocker;
	private LockscreenBackground background;
	private CancellationSignal cancellationSignal = new CancellationSignal();

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

	@Override
	protected void onResume() {
		super.onResume();
		listenForFingerPrintAuth();
		background.activityResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancellationSignal.cancel();
		background.activityPaused();
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
		// Or you might want to specifically ask users to disable their system screen ic_lock_black to get the best experience
		((KeyguardManager) getSystemService(KEYGUARD_SERVICE)).newKeyguardLock("IN").disableKeyguard();

		getWindow().setAttributes(localLayoutParams);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		wrapperView = new InterceptingRelativeLayout(getBaseContext());
		wrapperView.setOnTouchListener(this);
		View.inflate(this, R.layout.lock_screen, wrapperView);

		windowManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));
		windowManager.addView(wrapperView, localLayoutParams);

		background = new SensorBackground(this, wrapperView);
		LockscreenClock clock = new SimpleClock(wrapperView, this);
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

	public void unlock() {
		// Simple unlock by finishing activity and removing views
		wrapperView.animate().alpha(0f).setDuration(340).setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				removeAndFinish();
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				removeAndFinish();
			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		}).start();

	}

	private void removeAndFinish() {
		try {
			windowManager.removeView(wrapperView);
			wrapperView.removeAllViews();
			finish();
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	protected void tooManyFingerPrintTries() {
		cancellationSignal.cancel();
	}

	protected void listenForFingerPrintAuth() {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
			return;
		}
		FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);

		//ToDo: Don't do this here. Move to settings activity
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			Log.d(getClass().getSimpleName(), "Checking permission");
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.USE_FINGERPRINT}, 200);

			return;
		}

		if (!fingerprintManager.isHardwareDetected() || !fingerprintManager.hasEnrolledFingerprints()) {
			return;
		}

		fingerprintManager.authenticate(null, cancellationSignal = new CancellationSignal(), 0, new FingerprintManager.AuthenticationCallback() {
			@Override
			public void onAuthenticationError(int errorCode, CharSequence errString) {
				super.onAuthenticationError(errorCode, errString);
				Log.d(getClass().getSimpleName(), "Finger Auth error " + errorCode + "\n" + errString);

				tooManyFingerPrintTries();
			}

			@Override
			public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
				super.onAuthenticationHelp(helpCode, helpString);
				Toast.makeText(getBaseContext(), helpString, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
				super.onAuthenticationSucceeded(result);
				Log.d(getClass().getSimpleName(), "Finger Auth success");
				UtilHelper.vibrateSucces(getBaseContext());
				unlocker.unlockNoTouch();
			}

			@Override
			public void onAuthenticationFailed() {
				super.onAuthenticationFailed();
				UtilHelper.vibrateError(getBaseContext());
				listenForFingerPrintAuth();

				Log.d(getClass().getSimpleName(), "Finger Auth failed");

			}
		}, null);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (unlocker == null) {
			return false;
		}

		//Log.d(getClass().getSimpleName(), "TOUCH");

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
