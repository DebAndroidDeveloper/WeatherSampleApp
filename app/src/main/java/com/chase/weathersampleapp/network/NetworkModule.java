package com.chase.weathersampleapp.network;

import com.chase.weathersampleapp.BuildConfig;
import com.chase.weathersampleapp.callback.WeatherService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    @Provides
    @Singleton
    Retrofit provideCall() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.WEATHER_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public WeatherService providesWeatherService(
            Retrofit retrofit) {
        return retrofit.create(WeatherService.class);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public WeatherApiService providesWeatherApiService(
            WeatherService weatherService) {
        return new WeatherApiService(weatherService);
    }

}