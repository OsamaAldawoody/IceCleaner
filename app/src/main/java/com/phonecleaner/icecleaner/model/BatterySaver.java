package com.phonecleaner.icecleaner.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BatterySaver implements Parcelable {

    public static final int TYPE_SUPPER_SAVING = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_CUSTOM = 2;
    public static final int TYPE_ADD = 3;
    public static final int TYPE_NEW = 4;

    public static final int[] LENGTH_SCREEN_TIMEOUT = {15000, 30000,
            60000, 120000, 300000, 600000};

    private boolean selected;
    private String title;
    private boolean expand;
    private boolean autoScreenBrightness;
    private int lenghtScreenBrightness;
    private int lenghtScreenTimeOut;
    private boolean wifi;
    private boolean bluetooth;
    private boolean data;
    private boolean autoSync;
    private boolean vibration;
    private int type;

    public BatterySaver() {

    }

    public BatterySaver(boolean selected, String title, boolean expand, boolean autoScreenBrightness,
                        int lenghtScreenBrightness, int lenghtScreenTimeOut, boolean wifi, boolean bluetooth,
                        boolean data, boolean autoSync, boolean vibration, int type) {
        this.selected = selected;
        this.title = title;
        this.expand = expand;
        this.autoScreenBrightness = autoScreenBrightness;
        this.lenghtScreenBrightness = lenghtScreenBrightness;
        this.lenghtScreenTimeOut = lenghtScreenTimeOut;
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.data = data;
        this.autoSync = autoSync;
        this.vibration = vibration;
        this.type = type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public boolean isAutoScreenBrightness() {
        return autoScreenBrightness;
    }

    public void setAutoScreenBrightness(boolean autoScreenBrightness) {
        this.autoScreenBrightness = autoScreenBrightness;
    }

    public int getLenghtScreenBrightness() {
        return lenghtScreenBrightness;
    }

    public void setLenghtScreenBrightness(int lenghtScreenBrightness) {
        this.lenghtScreenBrightness = lenghtScreenBrightness;
    }

    public int getLenghtScreenTimeOut() {
        return lenghtScreenTimeOut;
    }

    public void setLenghtScreenTimeOut(int lenghtScreenTimeOut) {
        this.lenghtScreenTimeOut = lenghtScreenTimeOut;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(boolean bluetooth) {
        this.bluetooth = bluetooth;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
