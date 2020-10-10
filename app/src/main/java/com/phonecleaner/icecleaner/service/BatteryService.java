package com.phonecleaner.icecleaner.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;

public class BatteryService extends Service {

	public static final String NOTIFY_HOME = "com.battery.main";

	private RemoteViews mRemoteViews;
	private Notification mNotification;

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		getApplicationContext().registerReceiver(mBroadcastReceiver, iFilter);
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		newNotification(getApplicationContext());
		return START_STICKY;
	}

	private void newNotification(Context context) {
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		String title = context.getString(R.string.app_name);
		mRemoteViews = new RemoteViews(context.getPackageName(),
				R.layout.notification_battery);
		mNotification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.logo)
				.setContentIntent(resultPendingIntent).setContentTitle(title)
				.build();
		setListeners(context, mRemoteViews);
	}

	public void setListeners(Context context, RemoteViews view) {
		Intent intentHome = new Intent(NOTIFY_HOME);
		PendingIntent pendingIntentHome = PendingIntent.getBroadcast(context,
				0, intentHome, PendingIntent.FLAG_CANCEL_CURRENT);
		view.setOnClickPendingIntent(R.id.viewMainNotification,
				pendingIntentHome);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			float percentage = level / (float) scale;
			int progressBattery = (int) ((percentage) * 100);
			if (progressBattery > 100) {
				progressBattery = 100;
			}
			mRemoteViews.setTextViewText(R.id.tvPercentageNotification,
					String.valueOf(progressBattery));
			mRemoteViews.setProgressBar(R.id.progressBarBatteryNotification,
					100, progressBattery, false);

			mNotification.contentView = mRemoteViews;

			mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
			int NOTIFICATION_ID = 1111;
			startForeground(NOTIFICATION_ID, mNotification);
		}
	};
}