package com.phonecleaner.icecleaner.wiget;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.asyncTask.TaskMemoryBoost;
import com.phonecleaner.icecleaner.utils.Utils;
import com.phonecleaner.icecleaner.utils.WidgetUtils;

public class MyWidgetProvider extends AppWidgetProvider {
    private static final String TAG = MyWidgetProvider.class.getName();

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // initializing widget layout
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        // register for button event
        remoteViews.setOnClickPendingIntent(R.id.viewWigetMain,
                buildButtonPendingIntent(context));
        remoteViews.setViewVisibility(R.id.progressBarWidget, View.GONE);
        remoteViews.setViewVisibility(R.id.imgWiget, View.VISIBLE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryInfo(memInfo);
        final long availMem = memInfo.availMem;

        new TaskMemoryBoost(context, false, new TaskMemoryBoost.OnTaskMemoryBoostListener() {
            @Override
            public void onMemoryBoostSuccess(boolean result, long freeRam) {
                // updating view with initial data
                float totalRam = Utils.getTotalRam();
                int percentRam = (int) (((double) (totalRam - availMem) / (double) totalRam) * 100);
                remoteViews.setTextViewText(R.id.tvProgressWiget,
                        String.format(context.getString(R.string.percent), percentRam));
                // request for widget update
                pushWidgetUpdate(context, remoteViews);
            }
        }).execute();
    }

    public static PendingIntent buildButtonPendingIntent(Context context) {
        ++MyWidgetIntentReceiver.clickCount;

        // initiate widget update request
        Intent intent = new Intent();
        intent.setAction(WidgetUtils.WIDGET_UPDATE_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context,
                MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}
