package com.phonecleaner.icecleaner.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.widget.TextView;


import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.utils.StatusUtils;
import com.phonecleaner.icecleaner.utils.Utils;
import com.phonecleaner.icecleaner.view.ArcProgress;

/**
 * Copyright © 2016 AsianTech inc.
 * Created by HuongNV on 01/03/2016.
 */

public class TotalMemoryStorageTask extends AsyncTask<Void, Integer, Boolean> {

    private Context mContext;
    private ArcProgress mArcProgressMemory;
    private TextView mTvStorage;

    private long totalMemory;
    private long useMemory;

    public TotalMemoryStorageTask(Context context, ArcProgress arcProgressMemory, TextView tvStorage) {
        mContext = context;
        mArcProgressMemory = arcProgressMemory;
        mTvStorage = tvStorage;
        mArcProgressMemory.setMax(100);
        mArcProgressMemory.setProgress(StatusUtils.read(mContext, "percentMemory", 79));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        totalMemory = getTotalMemoryStorage();
        long availableMemory = getMemoryStorageAvailable();
        useMemory = totalMemory - availableMemory;
        int percentMemory = (int) (((double) useMemory / (double) totalMemory) * 100);
        StatusUtils.save(mContext, "percentMemory", percentMemory);
        if (percentMemory > 100) {
            percentMemory = percentMemory % 100;
        }
        for (int i = 0; i <= percentMemory; i++) {
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
        if (null != mTvStorage && values[0] == 0) {
            mTvStorage.setText(String.format(mContext.getString(R.string.index_storage),
                    Utils.formatSize(useMemory), Utils.formatSize(totalMemory)));
        }
        if (null != mArcProgressMemory) {
            mArcProgressMemory.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    private long getTotalMemoryStorage() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return (long) statFs.getBlockSize() * (long) statFs.getBlockCount();
    }

    private long getMemoryStorageAvailable() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
    }
}
