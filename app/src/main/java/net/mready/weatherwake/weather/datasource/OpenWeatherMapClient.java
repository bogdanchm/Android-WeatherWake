package net.mready.weatherwake.weather.datasource;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.widget.Toast;

import net.mready.core.util.Logger;
import net.mready.weatherwake.weather.models.WeatherCondition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenWeatherMapClient implements WeatherDataSource {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "1ebfcfd8977c15a8c5d7725ab6b9bf81";
    private final OkHttpClient okHttpClient;
    private final Handler mainHandler;
    private String querryType;


    public OpenWeatherMapClient(Context context) {
        this.mainHandler = new Handler(context.getMainLooper());
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

    }

    @Override
    public void setQuerryType(String type){
        querryType = type;
    }

    @Override
    public void getCurrentCondition(Location location, final Callback<ArrayList<WeatherCondition>> callback) {

        HttpUrl httpUrl = HttpUrl.parse(String.format("%s/%s", BASE_URL, querryType))
                .newBuilder()
                .addQueryParameter("lat", String.valueOf(location.getLatitude()))
                .addQueryParameter("lon", String.valueOf(location.getLongitude()))
                .addQueryParameter("units", "metric")
                .addQueryParameter("APPID", API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(e);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    if(querryType.equals("weather"))
                        mainHandler.post(() -> callback.onSuccess(parseWeatherJson(jsonObject)));
                    if(querryType.equals("forecast"))
                        mainHandler.post(() -> callback.onSuccess(parseForecastJson(jsonObject)));

                } catch (JSONException e) {
                    Logger.e(e);
                    mainHandler.post(callback::onError);
                }
            }
        });
    }

    private ArrayList<WeatherCondition> parseWeatherJson(JSONObject object) {
        ArrayList<WeatherCondition> list = new ArrayList<WeatherCondition>();
        WeatherCondition condition = new WeatherCondition();

        try {
            condition.setCity(object.getString("name"));

            JSONObject weatherObj = object.getJSONArray("weather").getJSONObject(0);
            condition.setType(getConditionType(weatherObj.getString("icon")));

            JSONObject mainObject = object.getJSONObject("main");

            condition.setTemp(mainObject.getDouble("temp"));
            condition.setTempMin(mainObject.getDouble("temp_min"));
            condition.setTempMax(mainObject.getDouble("temp_max"));

            condition.setDate(new Date(object.getLong("dt") * 1000));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        list.add(condition);
        return list;
    }

    private ArrayList<WeatherCondition> parseForecastJson(JSONObject object) {
        ArrayList<WeatherCondition> list = new ArrayList<WeatherCondition>();

        try {
            JSONArray weatherArray = object.getJSONArray("list");

            for (int i = 0; i < 5; i++) {
                JSONObject currentWeather = weatherArray.getJSONObject(i);
                WeatherCondition condition = new WeatherCondition();

                condition.setDate(new Date(currentWeather.getLong("dt")* 1000));
                condition.setTemp(currentWeather.getJSONObject("main").getDouble("temp"));
                condition.setType(getConditionType(currentWeather.getJSONArray("weather").getJSONObject(0).getString("icon")));

                list.add(condition);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private WeatherCondition.ConditionType getConditionType(String iconName) {
        if (iconName.startsWith("01")) {
            return WeatherCondition.ConditionType.CLEAR;
        } else if (iconName.startsWith("02")) {
            return WeatherCondition.ConditionType.FEW_CLOUDS;
        } else if (iconName.startsWith("03") || iconName.startsWith("04")) {
            return WeatherCondition.ConditionType.CLOUDS;
        } else if (iconName.startsWith("09")) {
            return WeatherCondition.ConditionType.SHOWER;
        } else if (iconName.startsWith("10")) {
            return WeatherCondition.ConditionType.RAIN;
        } else if (iconName.startsWith("11")) {
            return WeatherCondition.ConditionType.STORM;
        } else if (iconName.startsWith("13")) {
            return WeatherCondition.ConditionType.SNOW;
        } else if (iconName.startsWith("50")) {
            return WeatherCondition.ConditionType.MIST;
        }

        return WeatherCondition.ConditionType.CLEAR;
    }

}