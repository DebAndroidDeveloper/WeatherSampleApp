package com.chase.weathersampleapp.callback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chase.weathersampleapp.util.Constants;

public class WeatherDataReceiver extends BroadcastReceiver {
    private WeatherDataCallback mWeatherDataCallback;

    public WeatherDataReceiver(WeatherDataCallback weatherDataCallback) {
        this.mWeatherDataCallback = weatherDataCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.IntentActions.ACTION_ERROR)) {
            mWeatherDataCallback.onHttpResponseError(intent);
        } else if (intent.getAction().equals(Constants.IntentActions.ACTION_SUCCESS)) {
            mWeatherDataCallback.onHttpRequestComplete(intent);
        }
    }
}
