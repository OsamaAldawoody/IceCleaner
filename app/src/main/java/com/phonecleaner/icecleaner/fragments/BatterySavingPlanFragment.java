package com.phonecleaner.icecleaner.fragments;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.receiver.BatteryPlanStartReceiver;
import com.phonecleaner.icecleaner.receiver.BatteryPlanStopReceiver;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;

public class BatterySavingPlanFragment extends BaseFragment implements View.OnClickListener {

    public static final int TYPE_START = 1;
    public static final int TYPE_STOP = 0;
    public static final int TYPE_PERIOD = 0;
    public static final int TYPE_PERIOD_OUTSIDE = 1;

    private LinearLayout viewPediod;
    private LinearLayout viewPeriodOutside;

    private TextView tvTimeStart;
    private TextView tvTimeStop;
    private TextView tvModeToUse;
    private TextView tvModeToUsePeriod;
    private TextView tvTypeBatterySaver;
    private TextView tvTypeBatterySaverPeriod;

    private SwitchCompat switchMain;

    private boolean isEnbleMain;

    private int mTimeStarHour;
    private int mTimeStarMinute;
    private int mTimeStopHour;
    private int mTimeStopMinute;

    private String pediod;
    private String pediodOutside;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isEnbleMain = PreferenceUtil.getBoolean(getActivity(), PreferenceUtil.BATTERY_PLAN, false);
        mTimeStarHour = PreferenceUtil.getInt(getActivity(), PreferenceUtil.TIME_START_HOUR);
        mTimeStarMinute = PreferenceUtil.getInt(getActivity(), PreferenceUtil.TIME_START_MINUTE);
        mTimeStopHour = PreferenceUtil.getInt(getActivity(), PreferenceUtil.TIME_STOP_HOUR);
        mTimeStopMinute = PreferenceUtil.getInt(getActivity(), PreferenceUtil.TIME_STOP_MINUTE);
        pediod = PreferenceUtil.getString(getActivity(),
                PreferenceUtil.PEDIOD, getString(R.string.super_saving));
        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_INDEX, 0);
        pediodOutside = PreferenceUtil.getString(getActivity(),
                PreferenceUtil.PEDIOD_OUTSIDE, getString(R.string.normal));
        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_OUTSIDE_INDEX, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_alarm_battery, container, false);
        LinearLayout viewSwitch = (LinearLayout) view.findViewById(R.id.viewSwitch);
        viewPediod = (LinearLayout) view.findViewById(R.id.viewPediod);
        viewPeriodOutside = (LinearLayout) view.findViewById(R.id.viewPeriodOutside);

        tvTimeStart = (TextView) view.findViewById(R.id.tvTimeStart);
        tvTimeStop = (TextView) view.findViewById(R.id.tvTimeStop);
        tvModeToUse = (TextView) view.findViewById(R.id.tvModeToUse);
        tvModeToUsePeriod = (TextView) view.findViewById(R.id.tvModeToUsePeriod);
        tvTypeBatterySaver = (TextView) view.findViewById(R.id.tvTypeBatterySaver);
        tvTypeBatterySaverPeriod = (TextView) view.findViewById(R.id.tvTypeBatterySaverPeriod);

        switchMain = (SwitchCompat) view.findViewById(R.id.switchMain);
        switchMain.setChecked(isEnbleMain);
        setColorText(isEnbleMain);
        switchMain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtil.saveBoolean(getActivity(), PreferenceUtil.BATTERY_PLAN, isChecked);
                setColorText(isChecked);
            }
        });

        tvTimeStart.setOnClickListener(this);
        tvTimeStop.setOnClickListener(this);
        viewSwitch.setOnClickListener(this);
        viewPediod.setOnClickListener(this);
        viewPeriodOutside.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTextTimeStart(mTimeStarHour, mTimeStarMinute);
        setTextTimeStop(mTimeStopHour, mTimeStopMinute);

        tvTypeBatterySaver.setText(pediod);
        tvTypeBatterySaverPeriod.setText(pediodOutside);
    }

    private void setAlarmBatteryPlanStart(Context context) {
        Intent alarmIntent = new Intent(context, BatteryPlanStartReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mTimeStarHour);
        calendar.set(Calendar.MINUTE, mTimeStarMinute);
        manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setAlarmBatteryPlanStop(Context context) {
        Intent alarmIntent = new Intent(context, BatteryPlanStopReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, mTimeStopHour);
        calendar.set(Calendar.MINUTE, mTimeStopMinute);
        manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setTextTimeStart(int timeStarHour, int timeStarMinute) {
        StringBuilder stringBuilderStart = new StringBuilder();
        if (timeStarHour < 10) {
            stringBuilderStart.append("0").append(timeStarHour);
        } else {
            stringBuilderStart.append(timeStarHour);
        }
        if (timeStarMinute < 10) {
            stringBuilderStart.append(":0").append(timeStarMinute);
        } else {
            stringBuilderStart.append(":").append(timeStarMinute);
        }
        tvTimeStart.setText(stringBuilderStart.toString());
    }

    private void setTextTimeStop(int timeStopHour, int timeStopMinute) {
        StringBuilder stringBuilderStop = new StringBuilder();
        if (timeStopHour < 10) {
            stringBuilderStop.append("0").append(timeStopHour);
        } else {
            stringBuilderStop.append(timeStopHour);
        }
        if (timeStopMinute < 10) {
            stringBuilderStop.append(":0").append(timeStopMinute);
        } else {
            stringBuilderStop.append(":").append(timeStopMinute);
        }
        tvTimeStop.setText(stringBuilderStop.toString());
    }

    private void setColorText(boolean isChecked) {
        if (isChecked) {
            viewPediod.setEnabled(true);
            viewPeriodOutside.setEnabled(true);
            tvTimeStart.setEnabled(true);
            tvTimeStop.setEnabled(true);
            tvTimeStart.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            tvTimeStop.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            tvModeToUse.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            tvModeToUsePeriod.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        } else {
            viewPediod.setEnabled(false);
            viewPeriodOutside.setEnabled(false);
            tvTimeStart.setEnabled(false);
            tvTimeStop.setEnabled(false);
            tvTimeStart.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_500));
            tvTimeStop.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_500));
            tvModeToUse.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_500));
            tvModeToUsePeriod.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_500));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewSwitch:
                switchMain.performClick();
                break;
            case R.id.viewPediod:
                selectTypeBatterySaver(TYPE_PERIOD);
                break;
            case R.id.viewPeriodOutside:
                selectTypeBatterySaver(TYPE_PERIOD_OUTSIDE);
                break;
            case R.id.tvTimeStart:
                dialogTime(TYPE_START, mTimeStarHour, mTimeStarMinute);
                break;
            case R.id.tvTimeStop:
                dialogTime(TYPE_STOP, mTimeStopHour, mTimeStopMinute);
                break;
            default:
                break;
        }
    }

    private void dialogTime(final int type, int timeHourCurrent, int timeMinuteCurrent) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_time);

        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        timePicker.setCurrentHour(timeHourCurrent);
        timePicker.setCurrentMinute(timeMinuteCurrent);
        dialog.findViewById(R.id.tvCancelBattery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvSaveBattery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                if (type == TYPE_START) {
                    mTimeStarHour = hour;
                    mTimeStarMinute = minute;
                    setTextTimeStart(hour, minute);
                    PreferenceUtil.saveInt(getActivity(), PreferenceUtil.TIME_START_HOUR, hour);
                    PreferenceUtil.saveInt(getActivity(), PreferenceUtil.TIME_START_MINUTE, minute);
                    setAlarmBatteryPlanStart(getActivity());
                } else {
                    mTimeStopHour = hour;
                    mTimeStopMinute = minute;
                    setTextTimeStop(hour, minute);
                    PreferenceUtil.saveInt(getActivity(), PreferenceUtil.TIME_STOP_HOUR, hour);
                    PreferenceUtil.saveInt(getActivity(), PreferenceUtil.TIME_STOP_MINUTE, minute);
                    setAlarmBatteryPlanStop(getActivity());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void selectTypeBatterySaver(final int type) {
        final String[] options = {getString(R.string.super_saving), getString(R.string.normal),
                getString(R.string.custom)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_mode));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (type == TYPE_PERIOD) {
                    tvTypeBatterySaver.setText(options[item]);
                    PreferenceUtil.saveString(getActivity(), PreferenceUtil.PEDIOD, options[item]);
                    if (options[item].equals(getString(R.string.super_saving))) {
                        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_INDEX, 0);
                    } else if (options[item].equals(getString(R.string.normal))) {
                        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_INDEX, 1);
                    } else if (options[item].equals(getString(R.string.custom))) {
                        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_INDEX, 2);
                    }
                } else {
                    tvTypeBatterySaverPeriod.setText(options[item]);
                    PreferenceUtil.saveString(getActivity(), PreferenceUtil.PEDIOD_OUTSIDE, options[item]);
                    if (options[item].equals(getString(R.string.super_saving))) {
                        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_OUTSIDE_INDEX, 0);
                    } else if (options[item].equals(getString(R.string.normal))) {
                        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_OUTSIDE_INDEX, 1);
                    } else if (options[item].equals(getString(R.string.custom))) {
                        PreferenceUtil.saveInt(getActivity(), PreferenceUtil.PEDIOD_OUTSIDE_INDEX, 2);
                    }
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog;
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.battery_plan), MainActivity.HeaderBarType.TYPE_CLEAN);
    }
}
