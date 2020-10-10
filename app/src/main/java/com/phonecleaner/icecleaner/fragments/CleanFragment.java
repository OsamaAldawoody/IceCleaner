package com.phonecleaner.icecleaner.fragments;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.adapter.AnimatedExpandableListView;
import com.phonecleaner.icecleaner.adapter.CleanAdapter;
import com.phonecleaner.icecleaner.asyncTask.TaskClean;
import com.phonecleaner.icecleaner.model.ChildItem;
import com.phonecleaner.icecleaner.model.GroupItem;
import com.phonecleaner.icecleaner.utils.Utils;
import com.phonecleaner.icecleaner.view.RotateLoading;

public class CleanFragment extends BaseFragment {

    private static final String TAG = CleanFragment.class.getName();
    private AnimatedExpandableListView mRecyclerView;
    private TextView mTvTotalCache;
    private TextView mTvType;
    private TextView mTvTotalFound;
    private TextView mTvNoJunk;
    private Button mBtnCleanUp;

    private LinearLayout mViewLoading;
    private RotateLoading mRotateloadingApks;
    private RotateLoading mRotateloadingCache;
    private RotateLoading mRotateloadingDownloadFiles;
    private RotateLoading mRotateloadingLargeFiles;

    private long mTotalSizeSystemCache;
    private long mTotalSizeFiles;
    private long mTotalSizeApk;
    private long mTotalSizeLargeFiles;

    private ArrayList<File> mFileListLarge = new ArrayList<>();

    private ArrayList<GroupItem> mGroupItems = new ArrayList<>();
    private CleanAdapter mAdapter;

    private ScanApkFiles mScanApkFiles;
    private TaskScan mTaskScan;
    private ScanDownLoadFiles mScanDownLoadFiles;
    private ScanLargeFiles mScanLargeFiles;

    private boolean mIsFragmentPause;
    View view;
    public static final int EXTDIR_REQUEST_CODE = 1110,MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=300;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.fragment_clean, container, false);
        requestPerMission();
        return view;
    }
    public void requestPerMission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        }else{
            intView();
        }
    }
    public void intView(){
        mViewLoading = (LinearLayout) view.findViewById(R.id.viewLoading);

        mRotateloadingApks = (RotateLoading) view.findViewById(R.id.rotateloadingApks);
        mRotateloadingCache = (RotateLoading) view.findViewById(R.id.rotateloadingCache);
        mRotateloadingDownloadFiles = (RotateLoading) view.findViewById(R.id.rotateloadingDownload);
        mRotateloadingLargeFiles = (RotateLoading) view.findViewById(R.id.rotateloadingLargeFiles);

        mRecyclerView = (AnimatedExpandableListView) view.findViewById(R.id.recyclerView);
        mTvTotalCache = (TextView) view.findViewById(R.id.tvTotalCache);
        mTvType = (TextView) view.findViewById(R.id.tvType);
        mTvTotalFound = (TextView) view.findViewById(R.id.tvTotalFound);
        mTvNoJunk = (TextView) view.findViewById(R.id.tvNoJunk);
        mBtnCleanUp = (Button) view.findViewById(R.id.btnCleanUp);
        mBtnCleanUp.setVisibility(View.GONE);
        mBtnCleanUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanUp();
            }
        });

        initAdapter();
        if (mGroupItems.size() == 0) {
            mTvTotalFound.setText(String.format(getString(R.string.total_found),
                    getString(R.string.calculating)));
            Utils.setTextFromSize(0, mTvTotalCache, mTvType);
            mViewLoading.setVisibility(View.VISIBLE);
            startImageLoading();
            getFilesFromDirApkOld();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intView();
            } else {
                intView();
            }
        }
    }
    public void cleanUp() {
        for (int i = 0; i < mGroupItems.size() + 1; i++) {
            if (i == mGroupItems.size()) {
                replaceFragment(new CleanResultFragment(), false);
                return;
            }
            GroupItem groupItem = mGroupItems.get(i);
            if (groupItem.getType() == GroupItem.TYPE_FILE) {
                for (ChildItem childItem : groupItem.getItems()) {
                    if (childItem.isCheck()) {
                        File file = new File(childItem.getPath());
                        file.delete();
                        if (file.exists()) {
                            try {
                                file.getCanonicalFile().delete();
                                if (file.exists()) {
                                    getActivity().deleteFile(file.getName());
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "error delete file " + e);
                            }
                        }
                    }
                }
            } else {
                if (groupItem.isCheck()) {
                    new TaskClean(getActivity(), new TaskClean.OnTaskCleanListener() {
                        @Override
                        public void onCleanCompleted(boolean result) {
                            Log.i(TAG, "===--onCleanCompleted-->" + result);
                        }
                    }).execute();
                }
            }
        }
    }

    private void startImageLoading() {
        mRotateloadingApks.start();
        mRotateloadingCache.start();
        mRotateloadingDownloadFiles.start();
        mRotateloadingLargeFiles.start();
    }

    private void initAdapter() {
        mAdapter = new CleanAdapter(getActivity(), mGroupItems, new CleanAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(int groupPosition) {
                if (mRecyclerView.isGroupExpanded(groupPosition)) {
                    mRecyclerView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mRecyclerView.expandGroupWithAnimation(groupPosition);
                }
            }

            @Override
            public void onSelectItemHeader(int position, boolean isCheck) {
                changeCleanFileHeader(position, isCheck);
            }

            @Override
            public void onSelectItem(int groupPosition, int childPosition, boolean isCheck) {
                changeCleanFileItem(groupPosition, childPosition, isCheck);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void changeCleanFileHeader(int position, boolean isCheck) {
        long total = mGroupItems.get(position).getTotal();
        if (isCheck) {
            mTotalSizeSystemCache = mTotalSizeSystemCache + total;
        } else {
            mTotalSizeSystemCache = mTotalSizeSystemCache - total;
        }
        Utils.setTextFromSize(mTotalSizeSystemCache, mTvTotalCache, mTvType);
        mGroupItems.get(position).setIsCheck(isCheck);
        for (ChildItem childItem : mGroupItems.get(position).getItems()) {
            childItem.setIsCheck(isCheck);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void changeCleanFileItem(int groupPosition, int childPosition, boolean isCheck) {
        long total = mGroupItems.get(groupPosition).getItems().get(childPosition).getCacheSize();
        if (isCheck) {
            mTotalSizeSystemCache = mTotalSizeSystemCache + total;
        } else {
            mTotalSizeSystemCache = mTotalSizeSystemCache - total;
        }
        Utils.setTextFromSize(mTotalSizeSystemCache, mTvTotalCache, mTvType);
        mGroupItems.get(groupPosition).getItems().get(childPosition).setIsCheck(isCheck);
        boolean isCheckItem = false;
        for (ChildItem childItem : mGroupItems.get(groupPosition).getItems()) {
            isCheckItem = childItem.isCheck();
            if (!isCheckItem) {
                break;
            }
        }
        if (isCheckItem) {
            mGroupItems.get(groupPosition).setIsCheck(true);
        } else {
            mGroupItems.get(groupPosition).setIsCheck(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void getFilesFromDirApkOld() {
        if (mScanApkFiles != null
                && mScanApkFiles.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        mScanApkFiles = new ScanApkFiles(new OnScanApkFilesListener() {
            @Override
            public void onScanCompleted(List<File> result) {
                if (mIsFragmentPause) {
                    return;
                }
                if (result != null && result.size() > 0) {
                    GroupItem groupItem = new GroupItem();
                    groupItem.setTitle(getString(R.string.obsolete_apk));
                    groupItem.setIsCheck(false);
                    groupItem.setType(GroupItem.TYPE_FILE);
                    List<ChildItem> childItems = new ArrayList<>();
                    for (File currentFile : result) {
                        if (currentFile.getName().endsWith(".apk")) {
                            ChildItem childItem = new ChildItem(currentFile.getName(),
                                    currentFile.getName(), ContextCompat.getDrawable(getActivity(),
                                    R.drawable.ic_android_white_24dp),
                                    currentFile.length(), ChildItem.TYPE_APKS,
                                    currentFile.getPath(), false);
                            childItems.add(childItem);
                            mTotalSizeApk += currentFile.length();
                            groupItem.setTotal(mTotalSizeApk);
                            groupItem.setItems(childItems);
                        }
                    }
                    mGroupItems.add(groupItem);
                    mTvTotalFound.setText(String.format(getString(R.string.total_found),
                            Utils.formatSize(mTotalSizeApk)));
                }
                mRotateloadingApks.stop();
                getCacheFile();
            }
        });
        mScanApkFiles.execute();

    }

    private void getCacheFile() {
        if (mTaskScan != null
                && mTaskScan.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        mTaskScan = new TaskScan(new OnActionListener() {
            @Override
            public void onScanCompleted(long totalSize, List<ChildItem> result) {
                if (mIsFragmentPause) {
                    return;
                }
                mTotalSizeSystemCache = totalSize;
                Utils.setTextFromSize(totalSize, mTvTotalCache, mTvType);
                if (result.size() != 0) {
                    GroupItem groupItem = new GroupItem();
                    groupItem.setTitle(getString(R.string.system_cache));
                    groupItem.setTotal(mTotalSizeSystemCache);
                    groupItem.setIsCheck(true);
                    groupItem.setType(GroupItem.TYPE_CACHE);
                    groupItem.setItems(result);
                    mGroupItems.add(groupItem);
                    mTvTotalFound.setText(String.format(getString(R.string.total_found),
                            Utils.formatSize(mTotalSizeApk + mTotalSizeSystemCache)));
                    Utils.setTextFromSize(mTotalSizeSystemCache, mTvTotalCache, mTvType);
                }
                mRotateloadingCache.stop();
                getFilesFromDirFileDownload();
            }
        });
        mTaskScan.execute();
    }

    public void getFilesFromDirFileDownload() {
        if (mScanDownLoadFiles != null
                && mScanDownLoadFiles.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        mScanDownLoadFiles = new ScanDownLoadFiles(new OnScanDownloadFilesListener() {
            @Override
            public void onScanCompleted(File[] result) {
                if (mIsFragmentPause) {
                    return;
                }
                if (result != null && result.length > 0) {
                    GroupItem groupItem = new GroupItem();
                    groupItem.setTitle(getString(R.string.downloader_files));
                    groupItem.setIsCheck(false);
                    groupItem.setType(GroupItem.TYPE_FILE);
                    List<ChildItem> childItems = new ArrayList<>();
                    for (File currentFile : result) {
                        mTotalSizeFiles += currentFile.length();
                        ChildItem childItem = new ChildItem(currentFile.getName(),
                                currentFile.getName(), ContextCompat.getDrawable(getActivity(),
                                R.drawable.ic_android_white_24dp),
                                currentFile.length(), ChildItem.TYPE_DOWNLOAD_FILE,
                                currentFile.getPath(), false);
                        childItems.add(childItem);
                        groupItem.setTotal(mTotalSizeFiles);
                        groupItem.setItems(childItems);
                    }
                    mGroupItems.add(groupItem);
                }
                mRotateloadingDownloadFiles.stop();
                getLargeFile();
            }
        });
        mScanDownLoadFiles.execute();
    }

    private void getLargeFile() {
        if (mScanLargeFiles != null
                && mScanLargeFiles.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        mScanLargeFiles = new ScanLargeFiles(new OnScanLargeFilesListener() {
            @Override
            public void onScanCompleted(List<File> result) {
                if (mIsFragmentPause) {
                    return;
                }
                if (result.size() != 0) {
                    GroupItem groupItem = new GroupItem();
                    groupItem.setTitle(getString(R.string.large_files));
                    groupItem.setTotal(mTotalSizeLargeFiles);
                    groupItem.setIsCheck(false);
                    groupItem.setType(GroupItem.TYPE_FILE);
                    List<ChildItem> childItems = new ArrayList<>();
                    for (File currentFile : result) {
                        ChildItem childItem = new ChildItem(currentFile.getName(),
                                currentFile.getName(), ContextCompat.getDrawable(getActivity(),
                                R.drawable.ic_android_white_24dp),
                                currentFile.length(), ChildItem.TYPE_LARGE_FILES,
                                currentFile.getPath(), false);
                        childItems.add(childItem);
                        groupItem.setItems(childItems);
                    }
                    mGroupItems.add(groupItem);
                }
                mRotateloadingLargeFiles.stop();
                updateAdapter();
            }
        });
        mScanLargeFiles.execute();
    }

    private void updateAdapter() {
        if (mGroupItems.size() != 0) {
            for (int i = 0; i < mGroupItems.size(); i++) {
                if (mRecyclerView.isGroupExpanded(i)) {
                    mRecyclerView.collapseGroupWithAnimation(i);
                } else {
                    mRecyclerView.expandGroupWithAnimation(i);
                }
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mTvNoJunk.setVisibility(View.GONE);
            mBtnCleanUp.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mBtnCleanUp.setVisibility(View.GONE);
            mTvNoJunk.setVisibility(View.VISIBLE);
        }
        mViewLoading.setVisibility(View.GONE);
        mTvTotalFound.setText(String.format(getString(R.string.total_found),
                Utils.formatSize(mTotalSizeSystemCache + mTotalSizeFiles + mTotalSizeApk + mTotalSizeLargeFiles)));
    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File aListFile : listFile) {
                if (aListFile.isDirectory() && !aListFile.getName().equals(Environment.DIRECTORY_DOWNLOADS)) {
                    getfile(aListFile);
                } else {
                    long fileSizeInBytes = aListFile.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;
                    if (fileSizeInMB >= 10 && !aListFile.getName().endsWith(".apk")) {
                        mTotalSizeLargeFiles += aListFile.length();
                        mFileListLarge.add(aListFile);
                    }
                }
            }
        }
        return mFileListLarge;
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsFragmentPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsFragmentPause = false;
        setHeader(getString(R.string.clean_up), MainActivity.HeaderBarType.TYPE_CLEAN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGroupItems.clear();
        if (mScanApkFiles != null
                && mScanApkFiles.getStatus() == AsyncTask.Status.RUNNING) {
            mScanApkFiles.cancel(true);
        }
        if (mTaskScan != null
                && mTaskScan.getStatus() == AsyncTask.Status.RUNNING) {
            mTaskScan.cancel(true);
        }
        if (mScanDownLoadFiles != null
                && mScanDownLoadFiles.getStatus() == AsyncTask.Status.RUNNING) {
            mScanDownLoadFiles.cancel(true);
        }
        if (mScanLargeFiles != null
                && mScanLargeFiles.getStatus() == AsyncTask.Status.RUNNING) {
            mScanLargeFiles.cancel(true);
        }
    }

    private class TaskScan extends AsyncTask<Void, Integer, List<ChildItem>> {

        private Method mGetPackageSizeInfoMethod;
        private OnActionListener mOnActionListener;
        private long mTotalSize;

        public TaskScan(OnActionListener onActionListener) {
            mOnActionListener = onActionListener;
            try {
                mGetPackageSizeInfoMethod = getActivity().getPackageManager().getClass().getMethod(
                        "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<ChildItem> doInBackground(Void... params) {
            final List<ApplicationInfo> packages = getActivity().getPackageManager().getInstalledApplications(
                    PackageManager.GET_META_DATA);
            final CountDownLatch countDownLatch = new CountDownLatch(packages.size());
            final List<ChildItem> apps = new ArrayList<>();
            try {
                for (ApplicationInfo pkg : packages) {
                    mGetPackageSizeInfoMethod.invoke(getActivity().getPackageManager(), pkg.packageName,
                            new IPackageStatsObserver.Stub() {

                                @Override
                                public void onGetStatsCompleted(PackageStats pStats,
                                                                boolean succeeded)
                                        throws RemoteException {
                                    synchronized (apps) {
                                        addPackage(apps, pStats);
                                    }
                                    synchronized (countDownLatch) {
                                        countDownLatch.countDown();
                                    }
                                }
                            }
                    );
                }

                countDownLatch.await();
            } catch (InvocationTargetException | InterruptedException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return new ArrayList<>(apps);
        }

        @Override
        protected void onPostExecute(List<ChildItem> result) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanCompleted(mTotalSize, result);
            }
        }

        private void addPackage(List<ChildItem> apps, PackageStats pStats) {
            long cacheSize = pStats.cacheSize;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                cacheSize += pStats.externalCacheSize;
            }
            try {
                PackageManager packageManager = getActivity().getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(pStats.packageName,
                        PackageManager.GET_META_DATA);
                if (cacheSize > 1024 * 12) {
                    mTotalSize += cacheSize;
                    apps.add(new ChildItem(pStats.packageName,
                            packageManager.getApplicationLabel(info).toString(),
                            packageManager.getApplicationIcon(pStats.packageName),
                            cacheSize, ChildItem.TYPE_CACHE, null, true));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnActionListener {
        void onScanCompleted(long totalSize, List<ChildItem> result);
    }

    private class ScanLargeFiles extends AsyncTask<Void, Integer, List<File>> {

        private OnScanLargeFilesListener mOnScanLargeFilesListener;

        public ScanLargeFiles(OnScanLargeFilesListener onActionListener) {
            mOnScanLargeFilesListener = onActionListener;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<File> doInBackground(Void... params) {
            File root = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
            return getfile(root);
        }

        @Override
        protected void onPostExecute(List<File> result) {
            if (mOnScanLargeFilesListener != null) {
                mOnScanLargeFilesListener.onScanCompleted(result);
            }
        }
    }


    public interface OnScanLargeFilesListener {
        void onScanCompleted(List<File> result);
    }

    private class ScanApkFiles extends AsyncTask<Void, Integer, List<File>> {

        private OnScanApkFilesListener mOnScanLargeFilesListener;

        public ScanApkFiles(OnScanApkFilesListener onActionListener) {
            mOnScanLargeFilesListener = onActionListener;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<File> doInBackground(Void... params) {
            List<File> filesResult = new ArrayList<>();
            File downloadDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            File files[] = downloadDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".apk")) {
                        filesResult.add(file);
                    }
                }
            }

            return filesResult;
        }

        @Override
        protected void onPostExecute(List<File> result) {
            if (mOnScanLargeFilesListener != null) {
                mOnScanLargeFilesListener.onScanCompleted(result);
            }
        }
    }

    public interface OnScanApkFilesListener {
        void onScanCompleted(List<File> result);
    }

    private class ScanDownLoadFiles extends AsyncTask<Void, Integer, File[]> {

        private OnScanDownloadFilesListener mOnScanLargeFilesListener;

        public ScanDownLoadFiles(OnScanDownloadFilesListener onActionListener) {
            mOnScanLargeFilesListener = onActionListener;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected File[] doInBackground(Void... params) {
            File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            return downloadDir.listFiles();
        }

        @Override
        protected void onPostExecute(File[] result) {
            if (mOnScanLargeFilesListener != null) {
                mOnScanLargeFilesListener.onScanCompleted(result);
            }
        }
    }

    public interface OnScanDownloadFilesListener {
        void onScanCompleted(File[] result);
    }
}
