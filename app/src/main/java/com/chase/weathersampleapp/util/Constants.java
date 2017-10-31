package com.chase.weathersampleapp.util;

/**
 * Created by Debasis on 10/28/2017.
 */
public class Constants {
    public interface IntentExtras {
        String ERROR_NO_NETWORK = "com.chase.weathersampleapp.appIntentExtras.ERROR_NO_NETWORK";
        String MESSAGE = "com.chase.weathersampleapp.appIntentExtras.MESSAGE";
        String JSON_RESPONSE = "com.chase.weathersampleapp.appIntentExtras.JSON_RESPONSE";
        String REQUEST_ID = "com.chase.weathersampleapp.appIntentExtras.ID";
    }

    public interface IntentActions {
        String ACTION_ERROR = "com.chase.weathersampleapp.appIntentExtras.ACTION_ERROR";
        String ACTION_SUCCESS = "com.chase.weathersampleapp.appIntentExtras.ACTION_SUCCESS";
        String ACTION_GET_WEATHER_DATA = "com.chase.weathersampleapp.appIntentExtras.ACTION_GET_WEATHER_DATA";
    }

    public interface ApiMethods {
        String PREFERENCE_LOCATION = "com.chase.weathersampleapp.PREFERENCE_LOCATION";
    }

    public interface JsonKeys {
        String ERRORS = "errors";
        String MESSAGE = "message";
    }

    public interface ApiRequestId {
        int API_BASE_VALUE = 200;
    }
}
