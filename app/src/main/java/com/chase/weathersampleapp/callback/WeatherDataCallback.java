package com.chase.weathersampleapp.callback;

import android.content.Intent;

public interface WeatherDataCallback {
    /**
     * This method is called from broadcast receiver in case of an error
     *
     * @param intent
     */
    void onHttpResponseError(Intent intent);

    /**
     * This method is called from broadcast receive when data has been fetched
     */
    void onHttpRequestComplete(Intent intent);
}