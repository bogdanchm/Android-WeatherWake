package net.mready.weatherwake.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import net.mready.weatherwake.R;
import net.mready.weatherwake.alarms.AlarmsService;
import net.mready.weatherwake.ui.BaseActivity;
import net.mready.weatherwake.ui.adapters.AlarmsAdapter;
import net.mready.weatherwake.ui.helpers.WeatherHelper;
import net.mready.weatherwake.weather.datasource.OpenWeatherMapClient;
import net.mready.weatherwake.weather.datasource.WeatherDataSource;
import net.mready.weatherwake.weather.models.WeatherCondition;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends BaseActivity {

    private final static int REQ_PERMISSION_LOCATION = 234;

    private AlarmsAdapter alarmsAdapter;

    private Location lastLocation;

    private String[] days = {"Sun","Sun","Mon","Tue","Wed","Thurs","Fri"};
    private String[] months = {"Jan","Feb", "March","April","May","June","July","August","Sept","Oct","Nov","Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();

        checkLocationPermission();
    }

    private void setupViews() {
        ListView alarmsListView = (ListView) findViewById(R.id.list_alarms);

        View clockView = LayoutInflater.from(this).inflate(R.layout.item_home_clock, alarmsListView, false);

        alarmsListView.addHeaderView(clockView);

        alarmsAdapter = new AlarmsAdapter(this);
        alarmsListView.setAdapter(alarmsAdapter);

        alarmsListView.setOnItemClickListener((parent, view, position, id) -> {
            if (id == -1) {
                startActivity(new Intent(getApplicationContext(), TodayActivity.class));
            } else {
                Intent intent = new Intent(getApplicationContext(), AlarmDetailsActivity.class);
                intent.putExtra(AlarmsService.EXTRA_ALARM_ID, id);
                startActivity(intent);
            }
        });

        findViewById(R.id.rl_main).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TodayActivity.class));
        });

        findViewById(R.id.btn_new_alarm).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AlarmDetailsActivity.class));
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_PERMISSION_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadLocation();
            }
        }
    }

    @Override
    protected void onTimeChanged() {
        Calendar c = Calendar.getInstance();

        TextView tvTime = (TextView) findViewById(R.id.tv_time);
        TextView tvDate = (TextView) findViewById(R.id.tv_date);

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
                Toast.makeText(MainActivity.this, "Unable to load weather condition", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderCurrentCondition(WeatherCondition weatherCondition) {
        ((ViewAnimator) findViewById(R.id.switcher_weather)).setDisplayedChild(1);

        TextView tvCurrentWeather = (TextView) findViewById(R.id.tv_current_weather);
        TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        tvCurrentWeather.setText(WeatherHelper.getTemperatureString(weatherCondition.getTemp()));
        tvCurrentWeather.setCompoundDrawablesWithIntrinsicBounds(WeatherHelper.getIconForCondition(weatherCondition),
                0, 0, 0);

        tvLocation.setText(weatherCondition.getCity());
    }

    @Override
    protected void onStart() {
        super.onStart();

        onTimeChanged();
        alarmsAdapter.notifyDataSetChanged();
    }

}