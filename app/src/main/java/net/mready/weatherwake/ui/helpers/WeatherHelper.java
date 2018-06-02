package net.mready.weatherwake.ui.helpers;

import net.mready.weatherwake.R;
import net.mready.weatherwake.weather.models.WeatherCondition;

import java.util.Locale;

public class WeatherHelper {

    public static String getTemperatureString(double temp) {
        return String.format(Locale.getDefault(), "%.0f°C", temp);
    }

    public static int getIconForCondition(WeatherCondition weatherCondition) {
        switch (weatherCondition.getType()) {
            case CLEAR:
                return R.drawable.icon_w_s;
            case FEW_CLOUDS:
                return R.drawable.icon_w_cs;
            case CLOUDS:
                return R.drawable.icon_w_c;
            case SHOWER:
                return R.drawable.icon_w_cd;
            case RAIN:
                return R.drawable.icon_w_cr;
            case STORM:
                return R.drawable.icon_w_ch;
            default:
                return R.drawable.icon_w_cs;
        }
    }

}