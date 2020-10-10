package com.phonecleaner.icecleaner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.phonecleaner.icecleaner.model.BatterySaver;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;
import com.phonecleaner.icecleaner.utils.Utils;

import java.util.List;

public class BatteryPlanStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEnbale = PreferenceUtil.getBoolean(context, PreferenceUtil.BATTERY_PLAN, false);
        if (!isEnbale) {
            return;
        }
        PreferenceUtil preferenceUtil = new PreferenceUtil();
        int position = PreferenceUtil.getInt(context, PreferenceUtil.PEDIOD_INDEX);
        List<BatterySaver> batterySavers = preferenceUtil.getListBatterySaver(context);
        Utils.setBatterySaverSelected(context, batterySavers.get(position));
    }
}
