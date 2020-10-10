package com.phonecleaner.icecleaner.utils;


import android.content.Context;
import android.content.SharedPreferences;


import com.google.gson.Gson;
import com.phonecleaner.icecleaner.model.BatterySaver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferenceUtil {

    private static final String MyPREFERENCES = "MyPreferences";

    private static final String WHITE_LIST = "whitelist";

    public static final String BATTERY_SAVER = "battery_saver";
    public static final String FLOATING_BOOSTER = "floating_booster";
    public static final String BATTERY_PERCENT = "battery_percent";
    public static final String FULLY_BATTERY = "fully_battery";
    public static final String AUTO_BOOST = "auto_boost";
    public static final String BATTERY_PLAN = "battery_plan";
    public static final String TIME_START_HOUR = "time_start_hour";
    public static final String TIME_START_MINUTE = "time_start_minute";
    public static final String TIME_STOP_HOUR = "time_stop_hour";
    public static final String TIME_STOP_MINUTE = "time_stop_minutes";
    public static final String PEDIOD = "pediod";
    public static final String PEDIOD_INDEX = "pediod_index";
    public static final String PEDIOD_OUTSIDE = "pediod_outside";
    public static final String PEDIOD_OUTSIDE_INDEX = "pediod_outside_index";

    public PreferenceUtil() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveBatterySaver(Context context, List<BatterySaver> lockedApp) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonLockedApp = gson.toJson(lockedApp);
        editor.putString(BATTERY_SAVER, jsonLockedApp);
        editor.apply();
    }

    public void removeAll(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(BATTERY_SAVER).apply();
    }

    public List<BatterySaver> getListBatterySaver(Context context) {
        SharedPreferences settings;
        List<BatterySaver> batterySavers;

        settings = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);

        if (settings.contains(BATTERY_SAVER)) {
            String jsonLocked = settings.getString(BATTERY_SAVER, null);
            Gson gson = new Gson();
            BatterySaver[] items = gson.fromJson(jsonLocked,
                    BatterySaver[].class);
            batterySavers = Arrays.asList(items);
            batterySavers = new ArrayList<>(batterySavers);
        } else
            return null;
        return batterySavers;
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return pref.getBoolean(key, defaultValue);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    public static void saveLongTimeBoost(Context context, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE).edit();
        editor.putLong("timeboost", value);
        editor.apply();
    }

    public static long getLongTimeBoost(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return pref.getLong("timeboost", 0);
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String valueDefault) {
        SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return pref.getString(key, valueDefault);
    }

    // This four methods are used for maintaining favorites.
    public void saveLocked(Context context, List<String> lockedApp) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonLockedApp = gson.toJson(lockedApp);
        editor.putString(WHITE_LIST, jsonLockedApp);
        editor.apply();
    }

    public void addLocked(Context context, String app) {
        List<String> lockedApp = getLocked(context);
        if (lockedApp == null)
            lockedApp = new ArrayList<>();
        lockedApp.add(app);
        saveLocked(context, lockedApp);
    }

    public void removeLocked(Context context, String app) {
        ArrayList<String> locked = getLocked(context);
        if (locked != null) {
            locked.remove(app);
            saveLocked(context, locked);
        }
    }

    public ArrayList<String> getLocked(Context context) {
        SharedPreferences settings;
        List<String> locked;
        settings = context.getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        if (settings.contains(WHITE_LIST)) {
            String jsonLocked = settings.getString(WHITE_LIST, null);
            Gson gson = new Gson();
            String[] lockedItems = gson.fromJson(jsonLocked, String[].class);
            locked = Arrays.asList(lockedItems);
            locked = new ArrayList<>(locked);
        } else
            return null;
        return (ArrayList<String>) locked;
    }
}
