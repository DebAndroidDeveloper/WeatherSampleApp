package com.chase.weathersampleapp.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.chase.weathersampleapp.BuildConfig;
import com.chase.weathersampleapp.R;
import com.chase.weathersampleapp.util.CommonUtils;
import com.chase.weathersampleapp.util.Constants;
import com.chase.weathersampleapp.util.SharedPreferenceUtil;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class NetworkIntentService extends IntentService {

    public NetworkIntentService() {
        super("NetworkIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            NetworkManager networkManager = new NetworkManager(this);
            networkManager.httpGet(formApiUrl(this));
        }
    }

    public static void getWeatherData(Context context) {
        Intent getIntent = new Intent();
        getIntent.setAction(Constants.IntentActions.ACTION_GET_WEATHER_DATA);
        startApiService(context, getIntent);
    }

    private static void startApiService(Context context, Intent intent) {
        if (CommonUtils.isNetworkAvailable(context)) {
            intent.setClass(context, NetworkIntentService.class);
            context.startService(intent);
        } else {
            Intent errIntent = new Intent();
            errIntent.setAction(Constants.IntentActions.ACTION_ERROR);
            errIntent.putExtra(Constants.IntentExtras.MESSAGE, Constants.IntentExtras.ERROR_NO_NETWORK);
            context.sendBroadcast(errIntent);
        }
    }

    private String formApiUrl(Context context) {
        StringBuilder stringBuilder = new StringBuilder(BuildConfig.WEATHER_API_BASE_URL);
        //default location to get the weather
        String location = SharedPreferenceUtil.readPreference(context, Constants.ApiMethods.PREFERENCE_LOCATION, "Westervile,oh");
        stringBuilder.append(location);
        stringBuilder.append(",us");//default country
        stringBuilder.append("&units=imperial");
        stringBuilder.append("&APPID=");
        stringBuilder.append(context.getResources().getString(R.string.open_weather_api_key));
        return stringBuilder.toString();
    }
}
