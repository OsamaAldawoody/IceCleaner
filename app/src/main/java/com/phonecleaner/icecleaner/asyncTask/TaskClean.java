package com.phonecleaner.icecleaner.asyncTask;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import android.content.pm.IPackageDataObserver;
import android.Manifest;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class TaskClean extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "TaskClean";
    private Method mFreeStorageAndNotifyMethod;
    private Context mContext;
    private OnTaskCleanListener mOnTaskCleanListener;

    public TaskClean(Context context, OnTaskCleanListener onTaskCleanListener) {
        mContext = context;
        mOnTaskCleanListener = onTaskCleanListener;
        try {
            mFreeStorageAndNotifyMethod = context.getPackageManager().getClass().getMethod(
                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());

        try {
            if (canCleanInternalCache(mContext)) {
                mFreeStorageAndNotifyMethod.invoke(mContext.getPackageManager(),
                        (long) stat.getBlockCount() * (long) stat.getBlockSize(),
                        new IPackageDataObserver.Stub() {
                            @Override
                            public void onRemoveCompleted(String packageName, boolean succeeded)
                                    throws RemoteException {
                                countDownLatch.countDown();
                            }
                        }
                );
            } else {
                countDownLatch.countDown();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (isExternalStorageWritable()) {
                    final File externalDataDirectory = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath() + "/Android/data");

                    final String externalCachePath = externalDataDirectory.getAbsolutePath() +
                            "/%s/cache";

                    if (externalDataDirectory.isDirectory()) {
                        final File[] files = externalDataDirectory.listFiles();

                        for (File file : files) {
                            if (!deleteDirectory(new File(String.format(externalCachePath,
                                    file.getName())), true)) {
                                Log.e(TAG, "External storage suddenly becomes unavailable");

                                return false;
                            }
                        }
                    } else {
                        Log.e(TAG, "External data directory is not a directory!");
                    }
                } else {
                    Log.d(TAG, "External storage is unavailable");
                }
            }

            countDownLatch.await();
        } catch (InterruptedException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mOnTaskCleanListener != null) {
            mOnTaskCleanListener.onCleanCompleted(result);
        }
    }

    private boolean deleteDirectory(File file, boolean directoryOnly) {
        if (!isExternalStorageWritable()) {
            return false;
        }

        if (file == null || !file.exists() || (directoryOnly && !file.isDirectory())) {
            return true;
        }

        if (file.isDirectory()) {
            final File[] children = file.listFiles();

            if (children != null) {
                for (File child : children) {
                    if (!deleteDirectory(child, false)) {
                        return false;
                    }
                }
            }
        }

        file.delete();

        return true;
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean canCleanInternalCache(Context context) {
        return hasPermission(context, Manifest.permission.CLEAR_APP_CACHE);
    }

    public interface OnTaskCleanListener {
        void onCleanCompleted(boolean result);
    }
}
