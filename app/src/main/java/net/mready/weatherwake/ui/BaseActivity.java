package net.mready.weatherwake.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity {

    private final BroadcastReceiver timeUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onTimeChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        registerReceiver(timeUpdatedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(timeUpdatedReceiver);
        super.onStop();
    }

    @NonNull
    @Override
    public View findViewById(@IdRes int id) {
        //noinspection ConstantConditions
        return super.findViewById(id);
    }

    protected void onTimeChanged() {

    }

}