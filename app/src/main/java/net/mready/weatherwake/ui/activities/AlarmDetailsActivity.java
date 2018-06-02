package net.mready.weatherwake.ui.activities;

import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import net.mready.weatherwake.R;
import net.mready.weatherwake.alarms.AlarmsService;
import net.mready.weatherwake.alarms.datasource.AlarmsDataSource;
import net.mready.weatherwake.alarms.models.Alarm;
import net.mready.weatherwake.ui.BaseActivity;

import java.util.ArrayList;

public class AlarmDetailsActivity extends BaseActivity {

    private AlarmsDataSource dataSource;
    private Alarm alarm;

    private int[] btnDayIds = {R.id.btn_day_sun, R.id.btn_day_mon, R.id.btn_day_tue, R.id.btn_day_wed, R.id.btn_day_thu, R.id.btn_day_fri, R.id.btn_day_sat};

    private EditText txtAlarmName;
    private TextView tvAlarmTime;
    private int currentRingtone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        dataSource = new AlarmsDataSource(this);
        long alarmId = getIntent().getLongExtra(AlarmsService.EXTRA_ALARM_ID, 0);
        if (alarmId == 0) {
            alarm = new Alarm();
            alarm.setHour(9);
            alarm.setMinutes(0);
            alarm.setEnabled(true);
        } else {
            alarm = dataSource.get(alarmId);
        }

        setupViews();

    }

    private void selectRingtone(){
        RadioButton rdButton0 = (RadioButton) findViewById(R.id.rd_bn_0);
        RadioButton rdButton1 = (RadioButton) findViewById(R.id.rd_bn_1);
        RadioButton rdButton2 = (RadioButton) findViewById(R.id.rd_bn_2);
        RadioButton rdButton3 = (RadioButton) findViewById(R.id.rd_bn_3);

        ArrayList<RadioButton> list = new ArrayList<RadioButton>();
        list.add(rdButton0);
        list.add(rdButton1);
        list.add(rdButton2);
        list.add(rdButton3);

        for(RadioButton button : list) {
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    alarm.setRingtoneSelected(list.indexOf(button));

                    button.setBackgroundResource(R.drawable.radio_on);

                    for(RadioButton off : list){
                        if (off != button)
                            off.setBackgroundResource(R.drawable.radio_off);
                    }
                }
            });
        }
    }

    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();



        RadioButton rdButton0 = (RadioButton) findViewById(R.id.rd_bn_0);
        RadioButton rdButton1 = (RadioButton) findViewById(R.id.rd_bn_1);
        RadioButton rdButton2 = (RadioButton) findViewById(R.id.rd_bn_2);
        RadioButton rdButton3 = (RadioButton) findViewById(R.id.rd_bn_3);

        switch (v.getId()){
            case R.id.rd_bn_0:
                if(checked){
                    ((RadioButton) v).setBackgroundResource(R.drawable.radio_on);
                    alarm.setRingtoneSelected(0);
                    Toast.makeText(this,"0000000000", Toast.LENGTH_LONG).toString();
                    rdButton1.setBackgroundResource(R.drawable.radio_off);
                    rdButton2.setBackgroundResource(R.drawable.radio_off);
                    rdButton3.setBackgroundResource(R.drawable.radio_off);
                }
                break;
            case R.id.rd_bn_1:
                if(checked){
                    ((RadioButton) v).setBackgroundResource(R.drawable.radio_on);
                    alarm.setRingtoneSelected(1);
                    Toast.makeText(this,"11111111", Toast.LENGTH_LONG).toString();
                    rdButton0.setBackgroundResource(R.drawable.radio_off);
                    rdButton2.setBackgroundResource(R.drawable.radio_off);
                    rdButton3.setBackgroundResource(R.drawable.radio_off);
                }
                break;
            case R.id.rd_bn_2:
                if(checked){
                    ((RadioButton) v).setBackgroundResource(R.drawable.radio_on);
                    alarm.setRingtoneSelected(2);
                    Toast.makeText(this,"222222222", Toast.LENGTH_LONG).toString();
                    rdButton0.setBackgroundResource(R.drawable.radio_off);
                    rdButton1.setBackgroundResource(R.drawable.radio_off);
                    rdButton3.setBackgroundResource(R.drawable.radio_off);
                }
                break;
            case R.id.rd_bn_3:
                if(checked){
                    ((RadioButton) v).setBackgroundResource(R.drawable.radio_on);
                    alarm.setRingtoneSelected(3);
                    Toast.makeText(this,"333333333", Toast.LENGTH_LONG).toString();
                    rdButton0.setBackgroundResource(R.drawable.radio_off);
                    rdButton1.setBackgroundResource(R.drawable.radio_off);
                    rdButton2.setBackgroundResource(R.drawable.radio_off);
                }
                break;
        }
    }

    private void setupViews() {
        txtAlarmName = (EditText) findViewById(R.id.et_alarm_name);
        tvAlarmTime = (TextView) findViewById(R.id.tv_alarm_time);
       // daysOfWeek =  (TextView) findViewById(R.id.);

        tvAlarmTime.setText((Html.fromHtml(getString(R.string.time, alarm.getHour(), alarm.getMinutes()))));
        txtAlarmName.setText(alarm.getName());

        tvAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        SwitchCompat switchActive = (SwitchCompat) findViewById(R.id.sw_alarm_active);

        switchActive.setChecked(alarm.isEnabled());

        switchActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setEnabled(isChecked);
                dataSource.update(alarm);

                AlarmsService.setupNextAlarm(getApplicationContext(), true);
            }
        });

        for (int i = 0; i < btnDayIds.length; i++) {
            final int day = i + 1;

            ToggleButton btn = (ToggleButton) findViewById(btnDayIds[i]);

            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setTypeface(isChecked ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

                    if (isChecked) {
                        alarm.addDay(day);
                    } else {
                        alarm.removeDay(day);
                    }
                }
            });

            btn.setChecked(alarm.isDaySelected(day));
        }

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
            }
        });

    }

    private void showTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                alarm.setHour(hourOfDay);
                alarm.setMinutes(minute);

                tvAlarmTime.setText((Html.fromHtml(getString(R.string.time, alarm.getHour(), alarm.getMinutes()))));
            }
        }, alarm.getHour(), alarm.getMinutes(), true);

        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            dataSource.delete(alarm);
            finish();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    private void save() {
        String name = txtAlarmName.getText().toString();
        if (name.isEmpty()) {
            name = "Alarm";
        }

        alarm.setName(name);

        if (alarm.getId() == 0) {
            dataSource.insert(alarm);
        } else {
            dataSource.update(alarm);
        }

        AlarmsService.setupNextAlarm(this, true);
    }

}