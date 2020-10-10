package com.phonecleaner.icecleaner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.phonecleaner.icecleaner.model.BatterySaver;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;
import com.phonecleaner.icecleaner.utils.Utils;

import java.util.List;

public class BatteryPlanStopReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceUtil preferenceUtil = new PreferenceUtil();
        boolean isEnbale = PreferenceUtil.getBoolean(context, PreferenceUtil.BATTERY_PLAN, false);
        if (!isEnbale) {
            return;
        }
        int position = PreferenceUtil.getInt(context, PreferenceUtil.PEDIOD_OUTSIDE_INDEX);
        List<BatterySaver> batterySavers = preferenceUtil.getListBatterySaver(context);
        Utils.setBatterySaverSelected(context, batterySavers.get(position));
    }
}
