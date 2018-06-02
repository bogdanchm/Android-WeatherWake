package net.mready.weatherwake.alarms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.mready.core.util.Logger;
import net.mready.weatherwake.alarms.AlarmsService;
import net.mready.weatherwake.alarms.datasource.AlarmsDataSource;
import net.mready.weatherwake.alarms.models.Alarm;
import net.mready.weatherwake.ui.activities.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmId = intent.getLongExtra(AlarmsService.EXTRA_ALARM_ID, -1);
        if (alarmId == -1) {
            Logger.i("No alarm id");

            AlarmsService.setupNextAlarm(context, false);
            return;
        }

        AlarmsDataSource dataSource = new AlarmsDataSource(context);

        Alarm alarm = dataSource.get(alarmId);
        if (alarm == null) {
            Logger.i("Alarm", alarmId, "not found");
            AlarmsService.setupNextAlarm(context, false);
            return;
        }

        if (!alarm.isRepeating()) {
            alarm.setEnabled(false);
            dataSource.update(alarm);
        }

        AlarmsService.setupNextAlarm(context, false);

        Intent alarmAlert = new Intent(context, AlarmActivity.class);
        alarmAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        alarmAlert.putExtra(AlarmsService.EXTRA_ALARM_ID, alarmId);
        context.startActivity(alarmAlert);
    }

}