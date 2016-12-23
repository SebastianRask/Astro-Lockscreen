package net.nrask.voidlockscreen.helpers;

import android.content.Context;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 23/12/16.
 */

public class UtilHelper {
	public static void vibrateSucces(Context context) {
		SRJHelper.vibrate(50, context);
	}

	public static void vibrateError(Context context) {
		SRJHelper.vibratePattern(
				new long[] {
						0, 50, 100, 50
				},
				context
		);
	}
}
