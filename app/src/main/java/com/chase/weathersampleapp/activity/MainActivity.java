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
import com.chase.weathersampleapp.callback.WeatherDataReceiver;
import com.chase.weathersampleapp.model.WeatherData;
import com.chase.weathersampleapp.network.NetworkIntentService;
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

public class MainActivity extends BaseActivity implements WeatherDataCallback {

    private TextView cityField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    //    private TextView humidity_field;
//    private TextView pressure_field;
    private ImageView weatherIcon;
    private TextView updatedField;
    private WeatherDataReceiver weatherDataReceiver;
    private IntentFilter mFilter;
    private ProgressDialog mProgressDialog;
    private ImageLoader imageLoader;

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
//        humidity_field = (TextView)findViewById(R.id.humidity_field);
//        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        this.weatherDataReceiver = new WeatherDataReceiver(this);
        mFilter = new IntentFilter();
        mFilter.addAction(Constants.IntentActions.ACTION_ERROR);
        mFilter.addAction(Constants.IntentActions.ACTION_SUCCESS);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(config);
        NetworkIntentService.getWeatherData(this);
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
        this.registerReceiver(weatherDataReceiver, mFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(weatherDataReceiver);
    }

    @Override
    public void onHttpResponseError(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        String message = intent.getStringExtra(Constants.IntentExtras.MESSAGE);
        Log.e(getTag(), message);
        showErrorDialog(message);
    }

    @Override
    public void onHttpRequestComplete(Intent intent) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        String weatherData = intent.getStringExtra(Constants.IntentExtras.MESSAGE);

        //JsonReader jsonReader = new JsonReader( new StringReader(weatherData));
        try {
            JSONObject jsonObject = new JSONObject(weatherData);
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
            long sunset = 0L;

           /* jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                //String name = jsonReader.nextName();
                if(name.equals("main")){
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()){
                        String key = jsonReader.nextName();
                        if(key.equals("temp")){
                            double current_temp = jsonReader.nextDouble();
                            currentWeatherData.setCurrentTemp(String.valueOf(current_temp));
                        }else if(key.equals("pressure")){
                            pressure = jsonReader.nextInt();
                        }else if(key.equals("humidity")){
                            humidity = jsonReader.nextInt();
                        }else if(key.equals("temp_max")){
                            tempMax = jsonReader.nextDouble();
                        }else if(key.equals("temp_min")){
                            tempMin = jsonReader.nextDouble();
                        }else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                }else if(name.equals("wind")){
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()){
                        String key = jsonReader.nextName();
                        if(key.equals("speed")) {
                            speed = jsonReader.nextDouble();
                        }else {
                            jsonReader.skipValue();
                        }
                    }

                    jsonReader.endObject();
                }else if(name.equals("name")){
                    String city = jsonReader.nextString();
                    currentWeatherData.setCity(city);
                }else if(name.equals("sys")){
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        if(key.equals("sunrise")) {
                            sunrise = jsonReader.nextLong();
                        }else if(key.equals("sunset")){
                            sunset = jsonReader.nextLong();
                        }else if(key.equals("country")){
                            String country = jsonReader.nextString();
                            currentWeatherData.setCountry(country);
                        }else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                }else if(name.equals("weather") && jsonReader.peek() != JsonToken.NULL){
                    jsonReader.beginArray();
                    while(jsonReader.hasNext()){
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if(key.equals("main")) {
                                main = jsonReader.nextString();
                            }else if(key.equals("description")){
                                description = jsonReader.nextString();
                            }else if(key.equals("icon")){
                                String icon = jsonReader.nextString();
                                icon = icon + ".png";
                                imageLoader.displayImage(BuildConfig.WEATHER_API_IMAGE_URL + icon, weatherIcon);
                                currentWeatherData.setWeatherIcon(icon);
                            }else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                    }
                    jsonReader.endArray();
                }else if(name.equals("dt")){
                    long dateTime = jsonReader.nextLong();
                    DateFormat df = DateFormat.getDateTimeInstance();
                    String updatedOn = df.format(new Date(dateTime*1000));
                    updatedField.setText("Last update:  " + updatedOn);
                }
            }
            jsonReader.endObject();*/
            String weatherDetails = formatWeatherDetails(main, description, pressure, humidity, tempMax,
                    tempMin, speed, sunrise, sunset);
            currentWeatherData.setWeatherDetails(weatherDetails);
            this.cityField.setText(currentWeatherData.getCity() + ", " + currentWeatherData.getCountry());
            this.currentTemperatureField.setText(currentWeatherData.getCurrentTemp());
            this.detailsField.setText(weatherDetails);
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(jsonObject.getLong("dt") * 1000));
            updatedField.setText("Last update:  " + updatedOn);
            String icon = details.getString("icon");
            icon = icon + ".png";
            imageLoader.displayImage(BuildConfig.WEATHER_API_IMAGE_URL + icon, weatherIcon);
            Log.d(getTag(), weatherDetails);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getTag(), e.getMessage());
        } catch (Exception e) {
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
        NetworkIntentService.getWeatherData(this);
        mProgressDialog.show();
    }

    private String formatWeatherDetails(String main, String description, String pressure, String humidity,
                                        double tempMax, double tempMin, double speed, long sunrise, long sunset) {
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
