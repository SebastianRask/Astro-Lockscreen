package net.nrask.voidlockscreen.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.SRJService;
import net.nrask.voidlockscreen.services.StartLockscreenService;

import static android.app.admin.DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN;
import static android.app.admin.DevicePolicyManager.ACTION_SET_NEW_PASSWORD;

public class SetupActivity extends AppCompatActivity {
	private final int DATA_ACCESS_NONDIRECT_RESULT_CODE = 234;
	private final int NOTIFICATION_NONDIRECT_RESULT_CODE = 236;

	private final int NOTIFICATION_RESULT_CODE = 237;
	private final int SYSTEM_LOCK_RESULT_CODE = 238;
	private final int DATA_ACCESS_RESULT_CODE = 239;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_setup);

		if (!SRJService.isServiceRunning(StartLockscreenService.class, getBaseContext())) {
			startService(new Intent(getBaseContext(), StartLockscreenService.class));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case NOTIFICATION_RESULT_CODE | NOTIFICATION_NONDIRECT_RESULT_CODE:
				break;
			case SYSTEM_LOCK_RESULT_CODE:
				break;
			case DATA_ACCESS_RESULT_CODE | DATA_ACCESS_NONDIRECT_RESULT_CODE:
				break;
		}
	}

	public void onClickEnableNotifications(View sender) {
		try {
			startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), NOTIFICATION_RESULT_CODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onClickDisableSystemLockScreen(View sender) {
		try {
			startActivityForResult(new Intent(ACTION_SET_NEW_PASSWORD), SYSTEM_LOCK_RESULT_CODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onClickEnableDataAccess(View sender) {
		try {
			startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), DATA_ACCESS_RESULT_CODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}
}
