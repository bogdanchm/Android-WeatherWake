package net.mready.weatherwake.weather.models;

import java.util.Date;

public class WeatherCondition {

    public enum ConditionType {
        CLEAR, FEW_CLOUDS, CLOUDS, MIST, SHOWER, RAIN, STORM, SNOW
    }

    private String city;
    private Date date;

    private double temp;
    private double tempMin;
    private double tempMax;

    private ConditionType type;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

}