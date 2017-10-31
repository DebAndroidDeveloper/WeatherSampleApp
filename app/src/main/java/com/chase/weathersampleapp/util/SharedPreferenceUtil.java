package com.chase.weathersampleapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPreferenceUtil {
    private static final String TAG = SharedPreferenceUtil.class.getCanonicalName();


    /**
     * Save a key-value pair to SharedPreferences
     *
     * @param context The current context
     * @param key     The key for the key-value pair
     * @param value   The value of the key-value pair (String)
     */
    public static void savePreference(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
        Log.d(TAG, "Saved preference: " + key + " as " + value);
    }

    public static String readPreference(Context context, String key, String defaultValue) {
        Log.d(TAG, "readPreference : key :" + key
                + " ; defaultValue : " + defaultValue);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);

        Log.d(TAG, "readPreference : key :" + key + " ; value : "
                + sp.getString(key, defaultValue));

        return sp.getString(key, defaultValue);
    }
}