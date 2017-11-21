package com.chase.weathersampleapp.network;

import com.chase.weathersampleapp.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetworkModule.class,})
public interface NetworkComponent {
    void inject(MainActivity mainActivity);
}