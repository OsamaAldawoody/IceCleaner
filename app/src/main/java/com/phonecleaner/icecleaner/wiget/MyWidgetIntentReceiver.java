package com.phonecleaner.icecleaner.wiget;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.asyncTask.TaskMemoryBoost;
import com.phonecleaner.icecleaner.utils.Utils;
import com.phonecleaner.icecleaner.utils.WidgetUtils;

public class MyWidgetIntentReceiver extends BroadcastReceiver {
    public static int clickCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WidgetUtils.WIDGET_UPDATE_ACTION)) {
            updateWidgetPictureAndButtonListener(context);
        }
    }

    private void updateWidgetPictureAndButtonListener(final Context context) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        remoteViews.setViewVisibility(R.id.progressBarWidget, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.imgWiget, View.GONE);
        MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(),
                remoteViews);
        // re-registering for click listener
        remoteViews.setOnClickPendingIntent(R.id.viewWigetMain,
                MyWidgetProvider.buildButtonPendingIntent(context));

        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryInfo(memInfo);
        final long availMem = memInfo.availMem;
        // updating view
        new TaskMemoryBoost(context, true, new TaskMemoryBoost.OnTaskMemoryBoostListener() {
            @Override
            public void onMemoryBoostSuccess(boolean result, long freeRam) {
                // updating view with initial data
                float totalRam = Utils.getTotalRam();
                int percentRam = (int) (((double) (totalRam - availMem) / (double) totalRam) * 100);
                remoteViews.setTextViewText(R.id.tvProgressWiget,
                        String.format(context.getString(R.string.percent), percentRam));
                remoteViews.setViewVisibility(R.id.progressBarWidget, View.GONE);
                remoteViews.setViewVisibility(R.id.imgWiget, View.VISIBLE);
                // request for widget update
                MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(),
                        remoteViews);
                if (result) {
                    Toast.makeText(context, String.format(context.getString(R.string.free_ram),
                            Utils.formatSize(freeRam)), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.device_has_been_boosted),
                            Toast.LENGTH_LONG).show();
                }
            }
        }).execute();
    }
}
