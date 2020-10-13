package com.phonecleaner.icecleaner.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.phonecleaner.icecleaner.receiver.BoostReceiver;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BackgroundService getServerInstance() {
            return BackgroundService.this;
        }
    }


    @Override
    public void onCreate() {

        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
       initializeView();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                initializeView();
                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 5000);
//        Toast.makeText(context, "30000", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("BoostService");
        broadcastIntent.setClass(this, BoostReceiver.class);
        this.sendBroadcast(broadcastIntent);
        Log.v("BackgroundService","destroyed");
    }

    private void initializeView() {
        startService(new Intent(BackgroundService.this, FloatingWidgetService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
