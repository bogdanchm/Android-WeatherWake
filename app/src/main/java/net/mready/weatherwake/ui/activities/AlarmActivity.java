package net.mready.weatherwake.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import net.mready.core.util.Logger;
import net.mready.core.util.Strings;
import net.mready.weatherwake.R;
import net.mready.weatherwake.alarms.AlarmsService;
import net.mready.weatherwake.alarms.datasource.AlarmsDataSource;
import net.mready.weatherwake.alarms.models.Alarm;
import net.mready.weatherwake.ui.BaseActivity;

import java.util.Calendar;

public class AlarmActivity extends BaseActivity {

    private static final int ALARM_TIMEOUT_MS = 2 * 60 * 1000;

    private AlarmsDataSource dataSource;
    private Alarm alarm;

    private MediaPlayer mediaPlayer;
    private Handler stopHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        turnScreenOn();

        setContentView(R.layout.activity_alarm);

        setVolumeControlStream(AudioManager.STREAM_ALARM);

        stopHandler = new Handler();
        dataSource = new AlarmsDataSource(this);

        long alarmId = getIntent().getLongExtra(AlarmsService.EXTRA_ALARM_ID, 0);
        if (alarmId == 0) {
            Logger.e("Invalid alarm");
            finish();
        } else {
            alarm = dataSource.get(alarmId);
        }

        setupViews();

        playAlarm();
    }

    private void setupViews() {
        findViewById(R.id.btn_snooze).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                AlarmsService.snoozeAlarm(getApplicationContext(), alarm);
                finish();
            }
        });

        findViewById(R.id.btn_awake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                showToday();

            }
        });

        onTimeChanged();
    }

    @Override
    protected void onTimeChanged() {
        Calendar c = Calendar.getInstance();
        TextView tvTime = (TextView) findViewById(R.id.tv_time);
        tvTime.setText(Html.fromHtml(getString(R.string.time, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))));
    }

    private void turnScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void showToday() {
        startActivity(new Intent(getApplicationContext(), TodayActivity.class));
        finish();
    }

    private void playAlarm() {
        mediaPlayer = new MediaPlayer();

        Uri alertUri;

        if (Strings.isNullOrEmpty(alarm.getAlert())) {
            alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        } else {
            alertUri = Uri.parse(alarm.getAlert());
        }

        try {
            mediaPlayer.setDataSource(this, alertUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);

        } catch (Exception e) {
            Logger.e(this, e);
        }

        stopHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAlarm();
                showToday();
            }
        }, ALARM_TIMEOUT_MS);
    }

    private void stopAlarm() {
        if (stopHandler != null) {
            stopHandler.removeCallbacksAndMessages(null);
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}