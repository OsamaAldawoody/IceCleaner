package com.phonecleaner.icecleaner.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;

public class BatteryFullReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DEBUG", "----->>>ACTION_BATTERY_OKAY");
        boolean isFullyBattery = PreferenceUtil.getBoolean(context,
                PreferenceUtil.FULLY_BATTERY, false);
        if (!isFullyBattery) {
            return;
        }
        
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        float percentage = level / (float) scale;
        if (percentage == 100) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.drawable.logo);
            builder.setContentTitle(context.getString(R.string.app_name));
            builder.setContentText(context.getString(R.string.str_battery_about_full));

            Intent intentResult = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intentResult, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(100, builder.build());
        }
    }
}
