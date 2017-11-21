package com.chase.weathersampleapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chase.weathersampleapp.BuildConfig;
import com.chase.weathersampleapp.R;
import com.chase.weathersampleapp.callback.WeatherDataCallback;
import com.chase.weathersampleapp.model.Weather;
import com.chase.weathersampleapp.model.WeatherData;
import com.chase.weathersampleapp.network.DaggerNetworkComponent;
import com.chase.weathersampleapp.network.NetworkComponent;
import com.chase.weathersampleapp.network.NetworkModule;
import com.chase.weathersampleapp.network.WeatherApiService;
import com.chase.weathersampleapp.util.Constants;
import com.chase.weathersampleapp.util.SharedPreferenceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity implements WeatherDataCallback {

    private TextView cityField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private ImageView weatherIcon;
    private TextView updatedField;
    //    private WeatherDataReceiver weatherDataReceiver;
//    private IntentFilter mFilter;
    NetworkComponent networkComponent;
    @Inject
    WeatherApiService weatherApiService;
    private CompositeSubscription subscriptions;
    private ProgressDialog mProgressDialog;
    private ImageLoader imageLoader;
    private static final String DEFAULT_LOCATION = "Dallas,us";

    @Override
    public String getTag() {
        return MainActivity.class.getCanonicalName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        this.networkComponent = DaggerNetworkComponent.builder().networkModule(new NetworkModule()).build();
        this.networkComponent.inject(this);
        this.subscriptions = new CompositeSubscription();
        //default location to get the weather
        String location = SharedPreferenceUtil.readPreference(this, Constants.ApiMethods.PREFERENCE_LOCATION, DEFAULT_LOCATION);
        Subscription subscription = this.weatherApiService.getWeatherData(this, location, "imperial", this.getString(R.string.open_weather_api_key));
        subscriptions.add(subscription);
/*
        this.weatherDataReceiver = new WeatherDataReceiver(this);
        mFilter = new IntentFilter();
        mFilter.addAction(Constants.IntentActions.ACTION_ERROR);
        mFilter.addAction(Constants.IntentActions.ACTION_SUCCESS);
*/
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(config);
        //NetworkIntentService.getWeatherData(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("One moment please...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_city) {
            showInputDialog();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        this.registerReceiver(weatherDataReceiver, mFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        this.unregisterReceiver(weatherDataReceiver);
        subscriptions.unsubscribe();
    }

    @Override
    public void onHttpResponseError(Throwable exception) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        /*String message = intent.getStringExtra(Constants.IntentExtras.MESSAGE);
        Log.e(getTag(), message);*/
        showErrorDialog(exception.getMessage());
    }

    @Override
    public void onHttpRequestComplete(WeatherData weatherData) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        //String weatherData = intent.getStringExtra(Constants.IntentExtras.MESSAGE);

        try {

            /*JSONObject jsonObject = new JSONObject(weatherData);
            WeatherData currentWeatherData = new WeatherData();
            currentWeatherData.setCity(jsonObject.getString("name"));
            currentWeatherData.setCountry(jsonObject.getJSONObject("sys").getString("country"));
            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject mainObject = jsonObject.getJSONObject("main");

            currentWeatherData.setCurrentTemp(String.valueOf(mainObject.getDouble("temp")));
            String main = details.getString("main");
            String description = details.getString("description");
            String pressure = mainObject.getString("pressure");
            String humidity = mainObject.getString("humidity");
            double tempMax = mainObject.getDouble("temp_max");
            double tempMin = mainObject.getDouble("temp_min");
            double speed = jsonObject.getJSONObject("wind").getDouble("speed");
            long sunrise = 0L;
            long sunset = 0L;*/

            Weather weather = weatherData.getWeather().get(0);
            String main = null;
            String description = null;
            if (weather != null) {
                main = weather.getMain();
                description = weather.getDescription();
                String icon = weather.getIcon();
                icon = icon + ".png";
                imageLoader.displayImage(BuildConfig.WEATHER_API_IMAGE_URL + icon, weatherIcon);
            }

            String weatherDetails = formatWeatherDetails(main, description, String.valueOf(weatherData.getMain().getPressure()),
                    String.valueOf(weatherData.getMain().getHumidity()), weatherData.getMain().getTempMax(),
                    weatherData.getMain().getTempMin(), weatherData.getWind().getSpeed());
            this.cityField.setText(weatherData.getName() + ", " + weatherData.getSys().getCountry());
            this.currentTemperatureField.setText(String.valueOf(weatherData.getMain().getTemp()));
            this.detailsField.setText(weatherDetails);
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(weatherData.getDt() * 1000));
            updatedField.setText("Last update:  " + updatedOn);
            //String icon = details.getString("icon");
            Log.d(getTag(), weatherDetails);
            Log.d(getTag(), weatherData.getDt() + "");
        }
        /*catch (JSONException e) {
            e.printStackTrace();
            Log.e(getTag(), e.getMessage());
        }*/ catch (Exception e) {
            e.printStackTrace();
            Log.e(getTag(), e.getMessage());
        }
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Location");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeLocation(input.getText().toString());
            }
        });
        builder.show();
    }

    private void changeLocation(String location) {
        SharedPreferenceUtil.savePreference(this, Constants.ApiMethods.PREFERENCE_LOCATION, location);
        //call the open weather API and update the UI
        //NetworkIntentService.getWeatherData(this);
        Subscription subscription = this.weatherApiService.getWeatherData(this, location, "imperial", this.getString(R.string.open_weather_api_key));
        subscriptions.add(subscription);
        mProgressDialog.show();
    }

    private String formatWeatherDetails(String main, String description, String pressure, String humidity,
                                        double tempMax, double tempMin, double speed) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(main);
        stringBuilder.append("\n");
        stringBuilder.append(description);
        stringBuilder.append("\n");
        stringBuilder.append(tempMax + "/" + tempMin);
        stringBuilder.append("\n");
        /*stringBuilder.append("Sunrise         "+sunrise);
        stringBuilder.append("\n");
        stringBuilder.append("Sunset          "+sunset);
        stringBuilder.append("\n");*/
        stringBuilder.append("Wind            " + speed + " MPH");
        stringBuilder.append("\n");
        stringBuilder.append("Humidity        " + humidity + " %");
        stringBuilder.append("\n");
        stringBuilder.append("Pressure        " + pressure + "hPa");
        return stringBuilder.toString();
    }
}
