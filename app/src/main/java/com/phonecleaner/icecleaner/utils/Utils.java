package com.phonecleaner.icecleaner.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.model.BatterySaver;

public class Utils {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static String formatSize(long size) {
        if (size <= 0)
            return "0 MB";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void setTextFromSize(long size, TextView tvNumber, TextView tvType) {
        if (size <= 0) {
            tvNumber.setText(String.valueOf(0.00));
            tvType.setText("MB");
            return;
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        tvNumber.setText(String.valueOf(new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))));
        tvType.setText(units[digitGroups]);
    }

    public static void setTextScreenTimeOut(Context context, TextView textView, int time) {
        if (time < 60000) {
            textView.setText(String.format(context.getString(R.string.seconds), time / 1000));
        } else {
            textView.setText(String.format(context.getString(R.string.minutes), time / (1000 * 60)));
        }
    }

    public static boolean isUserApp(ApplicationInfo ai) {
        int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return (ai.flags & mask) == 0;
    }

    public static long getTotalRam() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            //total Memory
            initial_memory = Integer.valueOf(arrayOfString[1]) * 1024;
            localBufferedReader.close();
            return initial_memory;
        } catch (IOException e) {
            return -1;
        }
    }

    public static boolean checkLockedItem(Context context, String checkApp) {
        PreferenceUtil preferenceUtil = new PreferenceUtil();
        boolean check = false;
        List<String> locked = preferenceUtil.getLocked(context);
        if (locked != null) {
            for (String lock : locked) {
                if (lock.equals(checkApp)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    public static void rateApp(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="
                            + context.getPackageName())));
        }
    }

    public static void removeAds(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store")));
    }

    public static void shareApp(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id="
                        + context.getPackageName());
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void setBatterySaverSelected(Context context, BatterySaver batterySaver) {
        //set screen timeout
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, batterySaver.getLenghtScreenTimeOut());

        //set screen brightness
        if (batterySaver.isAutoScreenBrightness()) {
            Log.i("DEBUG", "");
//            android.provider.Settings.System.putInt(context.getContentResolver(),
//                    android.provider.Settings.System.SCREEN_BRIGHTNESS, mProgressBattery * 255 / 100);
        } else {
            int brightness = batterySaver.getLenghtScreenBrightness();
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness * 255 / 100);
        }
        //set wifi
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(batterySaver.isWifi());

        //set bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != bluetoothAdapter) {
            if (batterySaver.isBluetooth()) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        }

        //set data
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, batterySaver.isData());
        } catch (Exception ex) {
            Log.e("TAG", "Error setting mobile data state", ex);
        }

        //set auto sync
        ContentResolver.setMasterSyncAutomatically(batterySaver.isAutoSync());

        //set vibrate
        if (batterySaver.isVibration()) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }
}
