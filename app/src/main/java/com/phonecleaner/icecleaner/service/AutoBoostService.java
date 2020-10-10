package com.phonecleaner.icecleaner.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.asyncTask.TaskMemoryBoost;
import com.phonecleaner.icecleaner.utils.Utils;

public class AutoBoostService extends Service {

    public static final long LENGTH_TIMER = 10 * 60 * 1000;
    private Timer mTimer = new Timer();
    private boolean autoBoost;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        startService();
    }

    private void startService() {
        mTimer.scheduleAtFixedRate(new mainTask(), 0, LENGTH_TIMER);
    }

    private class mainTask extends TimerTask {
        public void run() {
            toastHandler.sendEmptyMessage(0);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        autoBoost = false;
    }

    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (autoBoost) {
                new TaskMemoryBoost(getApplicationContext(), true,
                        new TaskMemoryBoost.OnTaskMemoryBoostListener() {
                            @Override
                            public void onMemoryBoostSuccess(boolean result, long freeRam) {
                                if (result) {
                                    Toast.makeText(getApplicationContext(), String.format(getApplicationContext().getString(R.string.free_ram),
                                            Utils.formatSize(freeRam)), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.device_has_been_boosted),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }).execute();
            } else {
                autoBoost = true;
            }
        }
    };
}
