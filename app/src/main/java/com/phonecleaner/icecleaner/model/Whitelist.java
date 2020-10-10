package com.phonecleaner.icecleaner.model;

import android.graphics.drawable.Drawable;

public class Whitelist {

    private String packageName;
    private String applicationName;
    private Drawable icon;
    private boolean isCheck;

    public Whitelist(String packageName, String applicationName,
                     Drawable icon, boolean isCheck) {
        this.packageName = packageName;
        this.applicationName = applicationName;
        this.icon = icon;
        this.isCheck = isCheck;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
