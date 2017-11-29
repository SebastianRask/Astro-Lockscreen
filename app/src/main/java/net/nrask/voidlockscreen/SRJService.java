package net.nrask.voidlockscreen;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Sebastian Rask on 19-12-2016.
 */

public class SRJService {
	/**
	 * Check if a specific service is currently running
	 * Loops through all running services and compares class names
	 *
	 * @param serviceClass the class of the service you are unsure is running
	 * @return true if the service is running otherwise false.
	 */
	public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (manager == null) return false;

		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
