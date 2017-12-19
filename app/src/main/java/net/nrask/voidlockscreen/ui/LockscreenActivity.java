package net.nrask.voidlockscreen.ui;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.nrask.voidlockscreen.helpers.HomeKeyLocker;
import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.helpers.SRJHelper;
import net.nrask.voidlockscreen.ui.lockscreen.backgrounds.LockscreenBackground;
import net.nrask.voidlockscreen.ui.lockscreen.backgrounds.ParallaxBackground;
import net.nrask.voidlockscreen.ui.lockscreen.clocks.SimpleClock;
import net.nrask.voidlockscreen.helpers.UtilHelper;
import net.nrask.voidlockscreen.ui.lockscreen.notifications.LockscreenNotificationsView;
import net.nrask.voidlockscreen.ui.lockscreen.notifications.MaterialDesignNotifications;
import net.nrask.voidlockscreen.services.NotificationReaderService;
import net.nrask.voidlockscreen.services.StartLockscreenService;
import net.nrask.voidlockscreen.ui.lockscreen.unlockers.AcDisplayLockscreen;
import net.nrask.voidlockscreen.ui.lockscreen.unlockers.LockscreenUnlocker;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

public class LockscreenActivity extends Activity implements View.OnTouchListener {
	public static boolean running = false;

	public WindowManager windowManager;
	public RelativeLayout wrapperView;

	private LockscreenUnlocker unlocker;
	private LockscreenNotificationsView notificationsView;
	private LockscreenBackground background;
	private CancellationSignal cancellationSignal = new CancellationSignal();
	private ServiceConnection connection;
	private HomeKeyLocker homeKeyLocker = new HomeKeyLocker();

	private final int OVERLAY_PERMISSION_REQUEST = 8776;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setType(TYPE_SYSTEM_OVERLAY);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		startRequiredServices();

		try {
			showLockScreen();
		} catch (WindowManager.BadTokenException e) { // On Marshmallow and above this is an issue. We don't have the permission to draw over other applications even though it is declared in the manifest
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				checkDrawOverlayPermission();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		listenForFingerPrintAuth();
		if (background != null) {
			background.activityResumed();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		cancellationSignal.cancel();

		if (background != null) {
			background.activityPaused();
		}

		ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		if (am != null) {
			am.moveTaskToFront(getTaskId(), 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		running = false;
		if (notificationsView != null) {
			notificationsView.activityDestroyed();
		}

		if (connection != null) {
			unbindService(connection);
		}
	}

	@Override
	public void onUserLeaveHint() {
		super.onUserLeaveHint();
		Log.d("LockScreen", "onUserLeaveHint");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case OVERLAY_PERMISSION_REQUEST:
				// Check if user is back from accepting drawing permission
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
					recreate();
				} else {
					//Todo: Permission was denied. Show user that permission is absolutely needed.
				}
				break;
		}
	}

	private void startRequiredServices() {
		if (!SRJHelper.isServiceRunning(StartLockscreenService.class, getBaseContext())) {
			startService(new Intent(getBaseContext(), StartLockscreenService.class));
		}

		if (!SRJHelper.isServiceRunning(NotificationReaderService.class, getBaseContext())) {
			Intent notificationServiceIntent = new Intent(getBaseContext(), NotificationReaderService.class);
			startService(notificationServiceIntent);
			bindService(
					notificationServiceIntent,
					connection = new ServiceConnection() {
						@Override
						public void onServiceConnected(ComponentName name, IBinder service) {
							Log.d("NOTIF", "NLS Started");
							//NotificationReaderService.ServiceBinder binder = (NotificationReaderService.ServiceBinder)service;
						}

						@Override
						public void onServiceDisconnected(ComponentName name) {
							Log.d("NOTIF", "NLS Stopped");
						}},
					Context.BIND_AUTO_CREATE
			);
		}
	}

	private void showLockScreen() {
		Log.d(getClass().getName(), "ShowLockScreen beginning");
		wrapperView = new RelativeLayout(getBaseContext());
		windowManager = ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE));
		KeyguardManager km = ((KeyguardManager) getSystemService(KEYGUARD_SERVICE));

		if (wrapperView == null || windowManager == null || km == null) {
			//TODO: Show some kind of error
			return;
		}

		//homeKeyLocker.lock(this);

		// Unfortunately the flags WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD are not working on some devices (Thanks Samsung!)
		// So to be sure the keyguard is disabled, we disable the keyguard using this deprecated method
		// Or you might want to specifically ask users to disable their system screen ic_lock_black to get the best experience
		KeyguardManager.KeyguardLock lock = km.newKeyguardLock(getPackageName());
		lock.disableKeyguard();

		wrapperView.setOnTouchListener(this);

		View.inflate(this, R.layout.lock_screen, wrapperView);

		//background = new ParallaxBackground(LockscreenActivity.this, wrapperView);
		new SimpleClock(wrapperView, LockscreenActivity.this);
		notificationsView = new MaterialDesignNotifications(wrapperView, LockscreenActivity.this);
		unlocker = new AcDisplayLockscreen(wrapperView, LockscreenActivity.this);

		windowManager.addView(wrapperView, getWindowParams());

		Log.d(getClass().getName(), "ShowLockScreen end");
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private void checkDrawOverlayPermission() {
		if (!Settings.canDrawOverlays(this)) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST);
		}
	}

	private WindowManager.LayoutParams getWindowParams() {
		WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
		localLayoutParams.type = getWindowType();
		localLayoutParams.format = PixelFormat.TRANSLUCENT;
		localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
				WindowManager.LayoutParams.FLAG_FULLSCREEN |
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

		return localLayoutParams;
	}

	private int getWindowType() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? TYPE_APPLICATION_OVERLAY : TYPE_SYSTEM_ALERT;
	}

	public void unlockNoTouch() {
		unlocker.unlockNoTouch();
	}

	public void unlock() {
		wrapperView.animate().alpha(0f).setDuration(340).setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationEnd(Animator animator) {
				removeAndFinish();
			}

			@Override
			public void onAnimationCancel(Animator animator) {
				onAnimationEnd(animator);
			}

			@Override
			public void onAnimationStart(Animator animator) {}

			@Override
			public void onAnimationRepeat(Animator animator) {}
		}).start();

	}

	private void removeAndFinish() {
		try {
			homeKeyLocker.unlock();
			windowManager.removeView(wrapperView);
			wrapperView.removeAllViews();
			finishAffinity();
			overridePendingTransition(0, 0);
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

		if (fingerprintManager == null) {
			return;
		}

		//ToDo: Don't do this here. Move to settings activity
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
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
				UtilHelper.vibrateSuccess(getBaseContext());
				if (unlocker != null) {
					unlocker.unlockNoTouch();
				}
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

		// Send motion event to unlocker
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
