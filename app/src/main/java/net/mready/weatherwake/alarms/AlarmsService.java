package net.mready.weatherwake.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import net.mready.weatherwake.R;
import net.mready.weatherwake.alarms.datasource.AlarmsDataSource;
import net.mready.weatherwake.alarms.models.Alarm;
import net.mready.weatherwake.alarms.receivers.AlarmReceiver;

import java.util.Calendar;
import java.util.List;

public class AlarmsService {

    public static final String EXTRA_ALARM_ID = "mready.intent.extra.ALARM_ID";

    public static final int SNOOZE_TIME_MS = 10 * 60 * 1000;

    private static final String FILE_PREFS_ALARM = "alarms.prefs";
    private static final String PREF_KEY_SNOOZE_TIME = "snooze_time";
    private static final String PREF_KEY_SNOOZE_ALARM_ID = "snooze_alarm_id";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(FILE_PREFS_ALARM, Context.MODE_PRIVATE);
    }

    private static void setSnoozeAlert(Context context, Alarm alarm, long nextAlertTime) {
        SharedPreferences.Editor prefsEditor = getPrefs(context).edit();
        prefsEditor.putLong(PREF_KEY_SNOOZE_TIME, nextAlertTime);
        prefsEditor.putLong(PREF_KEY_SNOOZE_ALARM_ID, alarm.getId());
        prefsEditor.apply();
    }

    private static void clearSnoozeAlert(Context context) {
        SharedPreferences.Editor prefsEditor = getPrefs(context).edit();
        prefsEditor.putLong(PREF_KEY_SNOOZE_TIME, 0);
        prefsEditor.putLong(PREF_KEY_SNOOZE_ALARM_ID, 0);
        prefsEditor.apply();
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent buildAlarmPendingIntent(Context context, long alarmId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmsService.EXTRA_ALARM_ID, alarmId);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static long computeNextAlertTime(Alarm alarm) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        int nowHour = c.get(Calendar.HOUR_OF_DAY);
        int nowMinute = c.get(Calendar.MINUTE);

        if (alarm.getHour() < nowHour ||
                alarm.getHour() == nowHour && alarm.getMinutes() <= nowMinute) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        c.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        c.set(Calendar.MINUTE, alarm.getMinutes());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        if (alarm.isRepeating()) {
            int day = c.get(Calendar.DAY_OF_WEEK);
            int dayCount;
            for (dayCount = 0; dayCount < 7; dayCount++) {
                if (alarm.isDaySelected(day)) {
                    break;
                }
                day = (day % 7) + 1;
            }

            if (dayCount > 0) c.add(Calendar.DAY_OF_WEEK, dayCount);
        }

        return c.getTimeInMillis();
    }

    public static void snoozeAlarm(Context context, Alarm alarm) {
        long nextAlert = System.currentTimeMillis() + SNOOZE_TIME_MS;
        setSnoozeAlert(context, alarm, nextAlert);
        setupNextAlarm(context, true);
    }

    public static void setupNextAlarm(Context context, boolean showToast) {
        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        long now = System.currentTimeMillis();

        List<Alarm> alarms = dataSource.getAll();

        Alarm nextAlarm = null;
        long nextAlertTime = -1;

        for (Alarm alarm : alarms) {
            if (alarm.isEnabled()) {
                long alertTime = computeNextAlertTime(alarm);
                if (nextAlarm == null || alertTime < nextAlertTime) {
                    nextAlarm = alarm;
                    nextAlertTime = alertTime;
                }
            }
        }

        long snoozeAlarmId = getPrefs(context).getLong(PREF_KEY_SNOOZE_ALARM_ID, 0);
        if (snoozeAlarmId != 0) {
            Alarm snoozeAlarm = dataSource.get(snoozeAlarmId);
            long snoozeAlertTime = getPrefs(context).getLong(PREF_KEY_SNOOZE_TIME, 0);

            if (snoozeAlarm != null) {
                if (snoozeAlertTime < now) {
                    clearSnoozeAlert(context);
                } else if (nextAlarm == null || snoozeAlertTime < nextAlertTime) {
                    nextAlertTime = snoozeAlertTime;
                    nextAlarm = snoozeAlarm;
                    clearSnoozeAlert(context);
                }
            }
        }

        if (nextAlarm == null) {
            getAlarmManager(context).cancel(buildAlarmPendingIntent(context, 0));
            return;
        }

        getAlarmManager(context).set(AlarmManager.RTC_WAKEUP, nextAlertTime,
                buildAlarmPendingIntent(context, nextAlarm.getId()));

        if (showToast) {
            Toast.makeText(context, formatToastMessage(context, nextAlertTime), Toast.LENGTH_SHORT).show();
        }
    }

    private static String formatToastMessage(Context context, long timeInMillis) {
        long delta = timeInMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" :
                (days == 1) ? "1 day" :
                        Long.toString(days) + " days";

        String minSeq = (minutes == 0) ? "" :
                (minutes == 1) ? "1 minute" :
                        Long.toString(minutes) + " minutes";

        String hourSeq = (hours == 0) ? "" :
                (hours == 1) ? "1 hour" :
                        Long.toString(hours) + " hours";

        boolean displayDays = days > 0;
        boolean displayHour = hours > 0;
        boolean displayMinute = minutes > 0;

        int index = (displayDays ? 1 : 0) |
                (displayHour ? 2 : 0) |
                (displayMinute ? 4 : 0);

        String[] formats = context.getResources().getStringArray(R.array.alarm_set);
        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }

}