package com.chase.weathersampleapp.network;

import com.chase.weathersampleapp.model.Weather;
import com.chase.weathersampleapp.model.WeatherData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherServiceResponse {

    public List<Weather> getWeatherDataList() {
        return weatherDataList;
    }

    public void setWeatherDataList(List<Weather> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }

    @SerializedName("weather")
    @Expose
    private List<Weather> weatherDataList = null;
}