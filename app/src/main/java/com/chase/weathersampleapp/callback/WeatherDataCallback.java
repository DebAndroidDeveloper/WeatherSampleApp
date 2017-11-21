package com.chase.weathersampleapp.callback;

import android.content.Intent;

import com.chase.weathersampleapp.model.WeatherData;
import com.chase.weathersampleapp.network.WeatherServiceResponse;

public interface WeatherDataCallback {
    void onHttpResponseError(Throwable exception);

    /*
     */
    void onHttpRequestComplete(WeatherData weatherData);
}