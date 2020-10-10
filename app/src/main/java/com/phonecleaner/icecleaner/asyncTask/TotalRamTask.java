package com.phonecleaner.icecleaner.asyncTask;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.phonecleaner.icecleaner.utils.StatusUtils;
import com.phonecleaner.icecleaner.view.ArcProgress;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TotalRamTask extends AsyncTask<Void, Integer, Boolean> {

    private static final String TAG = TotalRamTask.class.getName();
    private Context mContext;
    private ArcProgress mArcProgressRam;

    public TotalRamTask(Context context, ArcProgress arcProgressRam) {
        mContext = context;
        mArcProgressRam = arcProgressRam;
        mArcProgressRam.setMax(100);
        mArcProgressRam.setProgress(StatusUtils.read(mContext, "percentRam", 80));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        long totalRam = getTotalRam();
        long availableRam = getAvaiableRam(mContext);
        long useRam = totalRam - availableRam;
        int percentRam = (int) (((double) useRam / (double) totalRam) * 100);

        if (percentRam > 100) {
            percentRam = percentRam % 100;
        }
        StatusUtils.save(mContext, "percentRam", percentRam);
        Log.i(TAG, "---percentRam-->>>>>" + percentRam);
        for (int i = 0; i <= percentRam; i++) {
            publishProgress(i);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (null != mArcProgressRam) {
            mArcProgressRam.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    private long getTotalRam() {
        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        long totRam = 0;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Integer.valueOf(value);
            // totRam = totRam / 1024;



        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return (totRam*1024);
    }

    private long getAvaiableRam(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getMemoryInfo(mi);
        }
        return mi.availMem;
    }
}
