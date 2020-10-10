package com.phonecleaner.icecleaner.asyncTask;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.phonecleaner.icecleaner.utils.Utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

public class TaskMemoryBoost extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = TaskMemoryBoost.class.getName();
    private Context mContext;
    private long beforeMemory;
    boolean isDelay;
    private OnTaskMemoryBoostListener mOnTaskMemoryBoostListener;

    public TaskMemoryBoost(Context context, boolean isDelay,
                           OnTaskMemoryBoostListener onTaskMemoryBoostListener) {
        mOnTaskMemoryBoostListener = onTaskMemoryBoostListener;
        mContext = context;
        this.isDelay = isDelay;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isDelay) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return isMemBoost();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        ActivityManager acm = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        acm.getMemoryInfo(memInfo);

        long aftermemory = memInfo.availMem;
        long ramFreed;
        if (aftermemory > beforeMemory) {
            ramFreed = (int) (aftermemory - beforeMemory);
        } else {
            ramFreed = 0;
        }
        mOnTaskMemoryBoostListener.onMemoryBoostSuccess(result, ramFreed);
    }

    // main boost operation
    private boolean isMemBoost() {

        PackageManager packageManager = mContext.getPackageManager();
        Method amethod[] = packageManager.getClass().getDeclaredMethods();
        int mLength = amethod.length;

        SharedPreferences pref = mContext.getSharedPreferences("CACHE",
                Context.MODE_PRIVATE);
        long l1 = pref.getLong("date_last", 0);// get last boost datetime
        long l2 = System.currentTimeMillis() - l1;
        if (l2 <= 600000 && l2 <= 7200000) {
            return false;// for recent boost, so we return false
        }

        long l3 = System.currentTimeMillis();
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("date_last", l3);// save last boost datetime
        editor.apply();

        if (l2 > 86400000) {
            l2 = 86400000;
        }
        int j = (int) (l2 / 1000L);
        int k = j / 360;

        Random random = new Random();
        int i1 = 0;
        int j2 = 0;
        int j1 = 0;
        int k1 = 0;
        int l4 = 0;
        if (k > 0) {
            i1 = j * k;
        } else {
            i1 = j;
        }

        j1 = i1 * 15;
        k1 = (j1 - i1) + 1;
        j2 = random.nextInt(k1) + i1;// get random cachce size
        amethod = packageManager.getClass().getDeclaredMethods();

        // for cache information
        if (mLength > 0) {
            Method method = amethod[0];
            if (!method.getName().equals("freeStorage")) {
                long l11 = 0L;
                l4 = 2;
                try {
                    Object aobj[] = new Object[l4];
                    Long long1 = l11;
                    aobj[0] = long1;
                    aobj[1] = 0;
                    Object aobj1[] = aobj;
                    Object obj = method.invoke(packageManager, aobj1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // process killer

        ActivityManager acm = (ActivityManager) mContext.getSystemService(
                Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        acm.getMemoryInfo(memInfo);
        beforeMemory = memInfo.availMem;

        List<ActivityManager.RunningAppProcessInfo> taskList = acm
                .getRunningAppProcesses();

        int before = taskList.size();

        for (int i = 0; i < taskList.size(); i++) {
            Log.v(TAG + i,
                    taskList.get(i).processName + " pid: "
                            + taskList.get(i).pid + " importance: "
                            + taskList.get(i).importance + " reason: "
                            + taskList.get(i).importanceReasonCode);
        }

        for (int i = 0; i < taskList.size(); i++) {

            ActivityManager.RunningAppProcessInfo process = taskList.get(i);
            int importance = process.importance;
            int pid = process.pid;
            String pname = process.processName;

            if (pname.equals(mContext.getPackageName())) {
                continue;
            }
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = packageManager.getApplicationInfo(pname, 128);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (applicationInfo != null
                    && Utils.isUserApp(applicationInfo)) {
                continue;
            }

			/*
             * if (importance == RunningAppProcessInfo.IMPORTANCE_SERVICE) {
			 * Log.v("manager: ", "task " + pname + " pid: " + pid +
			 * " has importance: " + importance + " WILL NOT KILL"); continue; }
			 */

            Log.v("manager", "task " + pname + " pid: " + pid
                    + " has importance: " + importance + " WILL KILL");

//            pList.add(pname);

            int count = 0;
            while (count < 3) {// attempt to kill three times
                acm.killBackgroundProcesses(taskList.get(i).processName);
                count++;
            }
        }

        taskList = acm.getRunningAppProcesses();// after killing processes
        int after = taskList.size();

        for (int i = 0; i < taskList.size(); i++) {// after killing tasks
            Log.v("proces after killings: " + i,
                    taskList.get(i).processName + " pid:" + taskList.get(i).pid
                            + " importance: " + taskList.get(i).importance
                            + " reason: "
                            + taskList.get(i).importanceReasonCode);
        }

//        int processesKilled = before - after;
//        long cacheFreed = j2;

        return true;
    }

    public interface OnTaskMemoryBoostListener {
        void onMemoryBoostSuccess(boolean result, long freeRam);
    }
}