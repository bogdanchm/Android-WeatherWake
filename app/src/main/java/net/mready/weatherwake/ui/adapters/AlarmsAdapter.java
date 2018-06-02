package net.mready.weatherwake.ui.adapters;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import net.mready.weatherwake.R;
import net.mready.weatherwake.alarms.AlarmsService;
import net.mready.weatherwake.alarms.datasource.AlarmsDataSource;
import net.mready.weatherwake.alarms.models.Alarm;

import java.util.List;

public class AlarmsAdapter extends BaseAdapter {

    private final Context context;
    private List<Alarm> alarms;
    private AlarmsDataSource dataSource;

    public AlarmsAdapter(Context context) {
        this.context = context;
        this.dataSource = new AlarmsDataSource(context);
        this.alarms = dataSource.getAll();
    }

    @Override
    public void notifyDataSetChanged() {
        this.alarms = dataSource.getAll();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Alarm getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return alarms.get(position).getId();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_alarm, parent, false);

            if (convertView == null) {
                throw new IllegalStateException();
            }
        }

        bindAlarmView(convertView, position);

        return convertView;
    }

    private void bindAlarmView(View view, int position) {
        final Alarm alarm = alarms.get(position);

        TextView tvAlarmTime = (TextView) view.findViewById(R.id.tv_alarm_time);
        tvAlarmTime.setText(Html.fromHtml(context.getString(R.string.time, alarm.getHour(), alarm.getMinutes())));

        TextView tvAlarmName = (TextView) view.findViewById(R.id.tv_alarm_name);
        tvAlarmName.setText(alarm.getName());

        TextView tvAlarmDays = (TextView) view.findViewById(R.id.tv_alarm_days);
        tvAlarmDays.setText(alarm.getDaysOfWeek());

        SwitchCompat swActive = (SwitchCompat) view.findViewById(R.id.sw_alarm_active);

        swActive.setChecked(alarm.isEnabled());
        swActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setEnabled(isChecked);
                dataSource.update(alarm);

                AlarmsService.setupNextAlarm(context, true);
            }
        });
    }

}