package com.chase.weathersampleapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chase.weathersampleapp.BuildConfig;
import com.chase.weathersampleapp.R;
import com.chase.weathersampleapp.callback.WeatherDataCallback;
import com.chase.weathersampleapp.model.Main;
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

import java.text.DateFormat;
import java.util.Date;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity implements WeatherDataCallback, TextView.OnEditorActionListener {

    private EditText searchEditText;
    private TextView cityField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private ImageView weatherIcon;
    private TextView updatedField;
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
        this.searchEditText = (EditText) findViewById(R.id.search_editText);
        this.searchEditText.setOnEditorActionListener(this);
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
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(config);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("One moment please...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
    }

    @Override
    public void onHttpResponseError(Throwable exception) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        showErrorDialog(exception.getMessage());
    }

    @Override
    public void onHttpRequestComplete(WeatherData weatherData) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }


        try {
            Weather weather = weatherData.getWeather().get(0);
            String mainWeather = null;
            String description = null;
            if (weather != null) {
                mainWeather = weather.getMain();
                description = weather.getDescription();
                String icon = weather.getIcon();
                icon = icon + ".png";
                imageLoader.displayImage(BuildConfig.WEATHER_API_IMAGE_URL + icon, weatherIcon);
            }

            Main main = weatherData.getMain();
            String pressure = null;
            String humidity = null;
            if (main != null) {
                if (main.getPressure() != null)
                    pressure = String.valueOf(main.getPressure());

                if (main.getHumidity() != null)
                    humidity = String.valueOf(main.getHumidity());
            }

            String weatherDetails = formatWeatherDetails(mainWeather, description, pressure,
                    humidity, weatherData.getMain().getTempMax(),
                    weatherData.getMain().getTempMin(), weatherData.getWind().getSpeed());
            this.cityField.setText(weatherData.getName() + ", " + weatherData.getSys().getCountry());
            this.currentTemperatureField.setText(String.valueOf(weatherData.getMain().getTemp()));
            this.detailsField.setText(weatherDetails);
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(weatherData.getDt() * 1000));
            updatedField.setText("Last update:  " + updatedOn);
            Log.d(getTag(), weatherDetails);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(getTag(), e.getMessage());
        }
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

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch();
            return true;
        }
        return false;
    }

    private void performSearch() {
        searchEditText.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        String searchQuery = searchEditText.getText().toString();
        Log.d(getTag(), "Search query : " + searchQuery);
        SharedPreferenceUtil.savePreference(this, Constants.ApiMethods.PREFERENCE_LOCATION, searchQuery);
        //call the open weather API and update the UI
        Subscription subscription = this.weatherApiService.getWeatherData(this, searchQuery, "imperial", this.getString(R.string.open_weather_api_key));
        subscriptions.add(subscription);
        mProgressDialog.show();
    }
}
