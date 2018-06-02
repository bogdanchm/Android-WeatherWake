package net.mready.weatherwake.alarms.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.mready.weatherwake.alarms.models.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmsDataSource {

    private AlarmsDb alarmsDb;

    public AlarmsDataSource(Context context) {
        this.alarmsDb = new AlarmsDb(context);
    }

    private Alarm fromCursor(Cursor cursor) {
        Alarm alarm = new Alarm();

        alarm.setId(cursor.getLong(cursor.getColumnIndexOrThrow(AlarmsDb.AlarmTable.COLUMN_ID)));
        alarm.setName(cursor.getString(cursor.getColumnIndexOrThrow(AlarmsDb.AlarmTable.COLUMN_NAME)));
        alarm.setHour(cursor.getInt(cursor.getColumnIndexOrThrow(AlarmsDb.AlarmTable.COLUMN_HOUR)));
        alarm.setMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(AlarmsDb.AlarmTable.COLUMN_MINUTES)));
        alarm.setEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(AlarmsDb.AlarmTable.COLUMN_ENABLED)) == 1);
        alarm.setAlert(cursor.getString(cursor.getColumnIndexOrThrow(AlarmsDb.AlarmTable.COLUMN_ALERT)));
        alarm.setDaysOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(AlarmsDb.AlarmTable.COLUMN_DAYSOFWEEK)));

        return alarm;
    }

    private ContentValues toContentValues(Alarm alarm) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(AlarmsDb.AlarmTable.COLUMN_NAME, alarm.getName());
        contentValues.put(AlarmsDb.AlarmTable.COLUMN_HOUR, alarm.getHour());
        contentValues.put(AlarmsDb.AlarmTable.COLUMN_MINUTES, alarm.getMinutes());
        contentValues.put(AlarmsDb.AlarmTable.COLUMN_ENABLED, alarm.isEnabled());
        contentValues.put(AlarmsDb.AlarmTable.COLUMN_ALERT, alarm.getAlert());
        contentValues.put(AlarmsDb.AlarmTable.COLUMN_DAYSOFWEEK, alarm.getDaysOfWeek());

        return contentValues;
    }

    public Alarm get(long id) {
        SQLiteDatabase db = alarmsDb.getReadableDatabase();

        Cursor cursor = db.query(AlarmsDb.AlarmTable.TABLE_NAME, null,
                AlarmsDb.AlarmTable.COLUMN_ID + " = " + id,
                null, null, null, null);

        Alarm alarm = null;
        if (cursor.moveToFirst()) {
            alarm = fromCursor(cursor);
        }
        cursor.close();
        db.close();

        return alarm;
    }

    public List<Alarm> getAll() {
        SQLiteDatabase db = alarmsDb.getReadableDatabase();

        Cursor cursor = db.query(AlarmsDb.AlarmTable.TABLE_NAME, null,
                null, null, null, null, null);

        List<Alarm> alarms = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                alarms.add(fromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return alarms;
    }

    public long insert(Alarm alarm) {
        SQLiteDatabase db = alarmsDb.getWritableDatabase();

        long id = db.insert(AlarmsDb.AlarmTable.TABLE_NAME, null, toContentValues(alarm));
        alarm.setId(id);

        db.close();

        return id;
    }

    public void update(Alarm alarm) {
        SQLiteDatabase db = alarmsDb.getWritableDatabase();

        db.update(AlarmsDb.AlarmTable.TABLE_NAME, toContentValues(alarm), AlarmsDb.AlarmTable.COLUMN_ID + " = " + alarm.getId(), null);

        db.close();
    }

    public void delete(Alarm alarm) {
        SQLiteDatabase db = alarmsDb.getWritableDatabase();

        db.delete(AlarmsDb.AlarmTable.TABLE_NAME, AlarmsDb.AlarmTable.COLUMN_ID + " = " + alarm.getId(), null);
        alarm.setId(0);

        db.close();
    }

}