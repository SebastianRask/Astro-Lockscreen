package net.nrask.voidlockscreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Sebastian on 19-12-2017.
 */

public class ApplicationSettings {
    @SuppressLint("StaticFieldLeak")
    private static ApplicationSettings instance;

    private Context context;

    public static ApplicationSettings getInstance() {
        if (instance == null) {
            instance = new ApplicationSettings();
        }
        return instance;
    }

    public ApplicationSettings initialize(Context context) {
        this.context = context.getApplicationContext();
        return this;
    }

    public boolean isLockscreenEnabled() {
        return isSetupCompleted();
    }

    /**
     * @return true if the user has completed the setup flow, otherwise false
     */
    public boolean isSetupCompleted() {
        return getValue(R.string.setting_user_setup_completed, Boolean.class, false);
    }

    public void setSetupCompleted(boolean value) {
        setValue(R.string.setting_user_setup_completed, value);
    }

    public SharedPreferences.Editor getEditor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit();
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


//    public void setValue(String key, boolean value) {
//        SharedPreferences.Editor editor = getEditor();
//        editor.putBoolean(key, value);
//        editor.commit();
//    }
//
//    public void setValue(String key, int value) {
//        SharedPreferences.Editor editor = getEditor();
//        editor.putInt(key, value);
//        editor.commit();
//    }
//
//    public void setValue(String key, String value) {
//        SharedPreferences.Editor editor = getEditor();
//        editor.putString(key, value);
//        editor.commit();
//    }


    public <T> void setValue(@StringRes int key, T value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(context.getString(key), new Gson().toJson(value));
        editor.commit();
    }

    public <T> void setValue(String key, T value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, new Gson().toJson(value));
        editor.commit();
    }

    public <T> T getValue(@StringRes int key, Class<T> type, T defaultValue) {
        return getValue(context.getString(key), type, defaultValue);
    }

    public <T> T getValue(String key, Class<T> type, T defaultValue) {
        SharedPreferences preferences = getPreferences();
        return preferences.contains(key) ? (T) new Gson().fromJson(preferences.getString(key, ""), type) : defaultValue;
    }

    public <T> T getValue(@StringRes int key, Type type, T defaultValue) {
        return getValue(context.getString(key), type, defaultValue);
    }

    public <T> T getValue(String key, Type type, T defaultValue) {
        SharedPreferences preferences = getPreferences();
        return preferences.contains(key) ? (T) new Gson().fromJson(preferences.getString(key, ""), type) : defaultValue;
    }
}
