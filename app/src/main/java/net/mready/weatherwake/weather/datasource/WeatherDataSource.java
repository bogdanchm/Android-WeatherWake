package net.mready.weatherwake.weather.datasource;

import android.location.Location;

import net.mready.weatherwake.weather.models.WeatherCondition;

import java.util.ArrayList;

public interface WeatherDataSource {

    interface Callback<T> {
        void onSuccess(ArrayList<WeatherCondition> arg);

        void onError();
    }

    void getCurrentCondition(Location location, Callback<ArrayList<WeatherCondition>> callback);

    void setQuerryType(String type);
}