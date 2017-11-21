package com.chase.weathersampleapp.callback;

import com.chase.weathersampleapp.model.WeatherData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface WeatherService {
    @GET("weather?")
    Observable<WeatherData> getWeatherData(@Query("q") String query, @Query("units") String unit,
                                           @Query("appid") String appId);
}