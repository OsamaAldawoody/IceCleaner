package com.phonecleaner.icecleaner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.phonecleaner.icecleaner.service.BackgroundService;

public class BoostReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, BackgroundService.class));
            Log.v("BoostReceiver", "boost receiver running");
        } else {
            Log.v("BoostReceiver", "boost receiver running");
            context.startService(new Intent(context, BackgroundService.class));
        }
    }
}
