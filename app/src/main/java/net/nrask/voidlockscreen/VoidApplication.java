package net.nrask.voidlockscreen;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by Sebastian on 27-11-2017.
 */

public class VoidApplication extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseCrash.log("Application created");
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d("LockScreen",	"Activity created " + activity);

    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("LockScreen",	"Activity started " + activity);

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("LockScreen",	"Activity resumed " + activity);

    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("LockScreen",	"Activity paused " + activity);

    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("LockScreen",	"Activity stopped " + activity);

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d("LockScreen",	"Activity destroyed " + activity);

    }
}
