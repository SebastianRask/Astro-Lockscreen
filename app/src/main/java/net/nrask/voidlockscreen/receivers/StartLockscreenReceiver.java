package net.nrask.voidlockscreen.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.nrask.voidlockscreen.activities.LockscreenActivity;

/**
 * Created by Sebastian Rask on 19-12-2016.
 */

public class StartLockscreenReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(getClass().getSimpleName(), "Lockscreen Intent received - " + intent.getAction());
		Intent lockscreenIntent = new Intent(context, LockscreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivity(lockscreenIntent);
	}
}
