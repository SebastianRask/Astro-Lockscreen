package net.nrask.voidlockscreen.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.nrask.voidlockscreen.receivers.StartLockscreenReceiver;

/**
 * Created by Sebastian Rask on 17-12-2016.
 */

public class StartLockscreenService extends Service {
	private BroadcastReceiver mReceiver = new StartLockscreenReceiver();

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {


		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}
