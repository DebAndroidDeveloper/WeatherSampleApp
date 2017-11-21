package com.chase.weathersampleapp.network;

import android.util.Log;

import com.chase.weathersampleapp.callback.WeatherDataCallback;
import com.chase.weathersampleapp.callback.WeatherService;
import com.chase.weathersampleapp.model.WeatherData;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherApiService {
    private WeatherService mWeatherService;
    private static final String TAG = WeatherApiService.class.getCanonicalName();

    public WeatherApiService(WeatherService weatherService) {
        this.mWeatherService = weatherService;
    }

    public Subscription getWeatherData(final WeatherDataCallback weatherDataCallback, String query,
                                       String units, String appId) {
        return this.mWeatherService.getWeatherData(query, units, appId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends WeatherData>>() {
                    @Override
                    public Observable<? extends WeatherData> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<WeatherData>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "HTTP Response complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                        weatherDataCallback.onHttpResponseError(e);
                    }

                    @Override
                    public void onNext(WeatherData weatherData) {
                        weatherDataCallback.onHttpRequestComplete(weatherData);
                    }
                });
    }
}