package net.mready.weatherwake.alarms.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmsDb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1;

    public interface AlarmTable {
        String TABLE_NAME = "alarm";

        String COLUMN_ID = "_id";
        String COLUMN_NAME = "name";
        String COLUMN_HOUR = "hour";
        String COLUMN_MINUTES = "minutes";
        String COLUMN_DAYSOFWEEK = "daysofweek";
        String COLUMN_ENABLED = "enabled";
        String COLUMN_ALERT = "alert";

        String CREATE_QUERY = "create table " + TABLE_NAME +
                "(" + COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_NAME + " text not null, " +
                COLUMN_HOUR + " integer not null, " +
                COLUMN_MINUTES + " integer not null, " +
                COLUMN_DAYSOFWEEK + " text not null, " +
                COLUMN_ENABLED + " integer not null, " +
                COLUMN_ALERT + " text)";

    }

    public AlarmsDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AlarmTable.CREATE_QUERY);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmTable.TABLE_NAME);
        onCreate(db);
    }

}