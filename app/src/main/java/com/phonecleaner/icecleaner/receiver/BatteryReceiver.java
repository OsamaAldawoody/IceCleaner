package com.phonecleaner.icecleaner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.service.BatteryService;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BatteryService.NOTIFY_HOME)) {
            closeSystemDialog(context);
            Intent intentMain = new Intent(context, MainActivity.class);
            intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intentMain);
        }
    }

    private void closeSystemDialog(Context context) {
        Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(intent);
    }

}
