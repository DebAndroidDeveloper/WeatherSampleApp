package com.chase.weathersampleapp.model;

public class WeatherData {
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeatherDetails() {
        return weatherDetails;
    }

    public void setWeatherDetails(String weatherDetails) {
        this.weatherDetails = weatherDetails;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String city;
    private String country;
    private String weatherDetails;
    private String currentTemp;
    private String weatherIcon;
}