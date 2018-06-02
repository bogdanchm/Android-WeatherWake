package net.mready.weatherwake.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import net.mready.weatherwake.R;

import net.mready.weatherwake.ui.BaseActivity;
import net.mready.weatherwake.ui.helpers.WeatherHelper;
import net.mready.weatherwake.weather.datasource.OpenWeatherMapClient;
import net.mready.weatherwake.weather.datasource.WeatherDataSource;
import net.mready.weatherwake.weather.models.WeatherCondition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class TodayActivity extends BaseActivity {

    private final static int REQ_PERMISSION_LOCATION = 234;

    private String[] days = {"Sun","Sun","Mon","Tue","Wed","Thurs","Fri"};
    private String[] months = {"Jan","Feb", "March","April","May","June","July","August","Sept","Oct","Nov","Dec"};

    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.today_layout);

        onTimeChanged();

        checkLocationPermission();

        loadForecast(44.4325, 26.103889);

        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.btn_thanks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TodayActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onTimeChanged() {
        Calendar c = Calendar.getInstance();

        TextView tvTime = (TextView) findViewById(R.id.tv_time_today);
        TextView tvDate = (TextView) findViewById(R.id.tv_date_today);

        tvTime.setText(Html.fromHtml(getString(R.string.time,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE))));
        tvDate.setText(Html.fromHtml(c.get(Calendar.DAY_OF_MONTH)+","+days[c.get(Calendar.DAY_OF_WEEK)]+" "+months[c.get(Calendar.MONTH)]));
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_PERMISSION_LOCATION);

        } else {
            loadLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void loadLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getApplicationContext());
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(1);
        locationRequest.setFastestInterval(10);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    lastLocation = location;
                    updateCurrentCondition();
                }
            }
        }, null);
    }

    private void updateCurrentCondition() {
        WeatherDataSource dataSource = new OpenWeatherMapClient(getApplicationContext());
        dataSource.setQuerryType("weather");

        dataSource.getCurrentCondition(lastLocation, new WeatherDataSource.Callback<ArrayList<WeatherCondition>>() {
            @Override
            public void onSuccess(final ArrayList<WeatherCondition> arg) {
                renderCurrentCondition(arg.get(0));
            }

            @Override
            public void onError() {
                Toast.makeText(TodayActivity.this, "Unable to load weather condition", Toast.LENGTH_SHORT).show();
            }
        });

        dataSource = new OpenWeatherMapClient(getApplicationContext());
        dataSource.setQuerryType("forecast");
        dataSource.getCurrentCondition(lastLocation, new WeatherDataSource.Callback<ArrayList<WeatherCondition>>() {
            @Override
            public void onSuccess(final ArrayList<WeatherCondition> arg) {
                renderCurrentCondition(arg);
            }

            @Override
            public void onError() {
                Toast.makeText(TodayActivity.this, "Unable to load weather condition", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderCurrentCondition(WeatherCondition weatherCondition) {

        TextView tvLocation = (TextView) findViewById(R.id.tv_location_today);

        tvLocation.setText(weatherCondition.getCity());

    }

    private void renderCurrentCondition(ArrayList<WeatherCondition> list) {

        Calendar calendar = GregorianCalendar.getInstance();
        ImageView ivWeather;
        TextView tvWeather;
        TextView tvHour;

        tvWeather = (TextView) findViewById(R.id.tv_temp_0);
        ivWeather = (ImageView) findViewById(R.id.iv_0);
        tvHour = (TextView) findViewById(R.id.tv_hour_0);
        WeatherCondition condition = list.get(0);
        calendar.setTime(condition.getDate());
        tvWeather.setText(WeatherHelper.getTemperatureString(condition.getTemp()));
        tvHour.setText(Html.fromHtml(getString(R.string.time,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE))));
        resolveImageCondition(condition,ivWeather);

        tvWeather = (TextView) findViewById(R.id.tv_temp_1);
        tvHour = (TextView) findViewById(R.id.tv_hour_1);
        condition = list.get(1);
        calendar.setTime(condition.getDate());
        tvWeather.setText(WeatherHelper.getTemperatureString(condition.getTemp()));
        tvHour.setText(Html.fromHtml(getString(R.string.time,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE))));
        resolveImageCondition(condition,ivWeather);

        tvWeather = (TextView) findViewById(R.id.tv_temp_2);
        tvHour = (TextView) findViewById(R.id.tv_hour_2);
        condition = list.get(2);
        calendar.setTime(condition.getDate());
        tvWeather.setText(WeatherHelper.getTemperatureString(condition.getTemp()));
        tvHour.setText(Html.fromHtml(getString(R.string.time,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE))));
        resolveImageCondition(condition,ivWeather);

        tvWeather = (TextView) findViewById(R.id.tv_temp_3);
        tvHour = (TextView) findViewById(R.id.tv_hour_3);
        condition = list.get(3);
        calendar.setTime(condition.getDate());
        tvWeather.setText(WeatherHelper.getTemperatureString(condition.getTemp()));
        tvHour.setText(Html.fromHtml(getString(R.string.time,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE))));
        resolveImageCondition(condition,ivWeather);

        tvWeather = (TextView) findViewById(R.id.tv_temp_4);
        tvHour = (TextView) findViewById(R.id.tv_hour_4);
        condition = list.get(4);
        calendar.setTime(condition.getDate());
        tvWeather.setText(WeatherHelper.getTemperatureString(condition.getTemp()));
        tvHour.setText(Html.fromHtml(getString(R.string.time,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE))));
        resolveImageCondition(condition,ivWeather);

    }

    private void resolveImageCondition(WeatherCondition condition, ImageView imageview){
        switch (condition.getType()){
            case CLEAR: imageview.setImageResource(R.drawable.icon_w_s); break;
            case FEW_CLOUDS: imageview.setImageResource(R.drawable.icon_w_cs); break;
            case CLOUDS: imageview.setImageResource(R.drawable.icon_w_c); break;
            case SHOWER: imageview.setImageResource(R.drawable.icon_w_cr); break;
            case RAIN: imageview.setImageResource(R.drawable.icon_w_crs); break;
            case STORM: imageview.setImageResource(R.drawable.icon_w_cw); break;
            case SNOW: imageview.setImageResource(R.drawable.icon_w_cws); break;
            case MIST: imageview.setImageResource(R.drawable.icon_w_cw); break;
        }

    }

    private void loadForecast(double lat, double lng) {

    }

}