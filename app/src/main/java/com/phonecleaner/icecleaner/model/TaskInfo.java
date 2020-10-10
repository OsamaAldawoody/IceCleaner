package com.phonecleaner.icecleaner.model;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;

public class TaskInfo implements Parcelable{

    private ApplicationInfo appinfo;
    public long mem;
    private PackagesInfo pkgInfo;
    private PackageManager pm;
    private ActivityManager.RunningAppProcessInfo runinfo;
    private String title;
    private CheckBox chkTask;
    private boolean chceked;

    public TaskInfo(Context context,
                    ActivityManager.RunningAppProcessInfo runinfo) {

        this.appinfo = null;
        this.pkgInfo = null;
        this.title = null;
        this.runinfo = runinfo;
        this.pm = context.getApplicationContext()
                .getPackageManager();
    }

    public TaskInfo(Context context, ApplicationInfo appinfo) {

        this.appinfo = null;
        this.pkgInfo = null;
        this.runinfo = null;
        this.title = null;
        this.appinfo = appinfo;
        this.pm = context.getApplicationContext()
                .getPackageManager();
    }

    public void getAppInfo() {
        if (appinfo == null) {
            try {
                String s = runinfo.processName;
                this.appinfo = pm.getApplicationInfo(s, 128);
            } catch (Exception e) {
            }
        }
    }

    public int getIcon() {
        return appinfo.icon;
    }

    public String getPackageName() {
        return appinfo.packageName;
    }

    public String getTitle() {

        if (title == null) {
            try {
                this.title = appinfo.loadLabel(pm).toString();
            } catch (Exception e) {

            }
        }
        return title;
    }

    public void setMem(long mem) {
        this.mem = mem;
    }

    public long getMem() {
        return mem;
    }

    public boolean isGoodProcess() {

        if (appinfo != null) {
            return true;
        } else {
            return false;
        }
    }

    public ApplicationInfo getAppinfo() {
        return appinfo;
    }

    public void setAppinfo(ApplicationInfo appinfo) {
        this.appinfo = appinfo;
    }

    public PackagesInfo getPkgInfo() {
        return pkgInfo;
    }

    public void setPkgInfo(PackagesInfo pkgInfo) {
        this.pkgInfo = pkgInfo;
    }

    public ActivityManager.RunningAppProcessInfo getRuninfo() {
        return runinfo;
    }

    public void setRuninfo(ActivityManager.RunningAppProcessInfo runinfo) {
        this.runinfo = runinfo;
    }

    public boolean isChceked() {
        return chceked;
    }

    public void setChceked(boolean chceked) {
        this.chceked = chceked;
    }

    public void setChkTask(CheckBox chkTask) {
        this.chkTask = chkTask;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
