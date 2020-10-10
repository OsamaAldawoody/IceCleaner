package com.phonecleaner.icecleaner.asyncTask;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.phonecleaner.icecleaner.model.TaskInfo;
import com.phonecleaner.icecleaner.view.DonutProgress;

import java.util.List;

public class TaskBoost extends AsyncTask<Void, Void, Void> {

    private static final String TAG = TaskBoost.class.getName();
    private DonutProgress mProgressBar;
    private List<TaskInfo> mTaskInfos;
    private ActivityManager mActivityManager;
    private OnTaskBoostListener mOnTaskBoostListener;
    private int mPercent;

    public TaskBoost(Context context, DonutProgress progressBar,
                     List<TaskInfo> taskInfos, OnTaskBoostListener onTaskBoostListener) {
        mProgressBar = progressBar;
        mTaskInfos = taskInfos;
        mOnTaskBoostListener = onTaskBoostListener;
        mActivityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        mPercent = 100 / mTaskInfos.size();
    }

    @Override
    protected void onPreExecute() {
        if (null != mProgressBar) {
            mProgressBar.setProgress(20);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (TaskInfo taskInfo : mTaskInfos) {
            if (taskInfo.isChceked()) {
                Log.d(TAG, "==killBackgroundProcesses--=>>" + taskInfo.getAppinfo().packageName);
                mActivityManager.killBackgroundProcesses(taskInfo.getAppinfo().packageName);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (null != mProgressBar) {
            mProgressBar.setProgress(mProgressBar.getProgress() + mPercent);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if (null != mProgressBar) {
            mProgressBar.setProgress(100);
        }
        if (null != mOnTaskBoostListener) {
            mOnTaskBoostListener.OnResult();
        }
    }

    public interface OnTaskBoostListener {
        void OnResult();
    }
}