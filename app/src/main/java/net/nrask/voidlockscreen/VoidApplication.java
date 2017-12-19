package net.nrask.voidlockscreen;

import android.app.Application;

/**
 * Created by Sebastian on 27-11-2017.
 */

public class VoidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationSettings.getInstance().initialize(getApplicationContext());
    }
}
