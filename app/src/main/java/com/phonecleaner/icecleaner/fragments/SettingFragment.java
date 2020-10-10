package com.phonecleaner.icecleaner.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.service.AutoBoostService;
import com.phonecleaner.icecleaner.service.BatteryService;
import com.phonecleaner.icecleaner.service.ChatHeadService;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;
import com.phonecleaner.icecleaner.utils.Utils;


public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = SettingFragment.class.getName();

    private CheckBox checkboxAutoBoost;
    private CheckBox checkboxFloating;
    private CheckBox checkboxBatteryPercent;
    private CheckBox checkboxFullyBattery;

    private TextView tvAutoBoost;
    private TextView tvFloatingBooster;
    private TextView tvBatteryPercent;
    private TextView tvFully;

    private long mLastTimeWidget = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        LinearLayout viewAutoBoost = (LinearLayout) root.findViewById(R.id.viewAutoBoost);
        LinearLayout viewWhiteList = (LinearLayout) root.findViewById(R.id.viewWhiteList);
        LinearLayout viewBoostWidget = (LinearLayout) root.findViewById(R.id.viewBoostWidget);
        LinearLayout viewFloatingBooster = (LinearLayout) root.findViewById(R.id.viewFloatingBooster);
        LinearLayout viewBatteryPercent = (LinearLayout) root.findViewById(R.id.viewBatteryPercent);
        LinearLayout viewFully = (LinearLayout) root.findViewById(R.id.viewFully);
        LinearLayout viewRate = (LinearLayout) root.findViewById(R.id.viewRate);
        LinearLayout viewShare = (LinearLayout) root.findViewById(R.id.viewShare);

        checkboxAutoBoost = (CheckBox) root.findViewById(R.id.checkboxAutoBoost);
        checkboxFloating = (CheckBox) root.findViewById(R.id.checkboxFloating);
        checkboxBatteryPercent = (CheckBox) root.findViewById(R.id.checkboxBatteryPercent);
        checkboxFullyBattery = (CheckBox) root.findViewById(R.id.checkboxFullyBattery);

        tvAutoBoost = (TextView) root.findViewById(R.id.tvAutoBoost);
        tvFloatingBooster = (TextView) root.findViewById(R.id.tvFloatingBooster);
        tvBatteryPercent = (TextView) root.findViewById(R.id.tvBatteryPercent);
        tvFully = (TextView) root.findViewById(R.id.tvFully);

        viewAutoBoost.setOnClickListener(this);
        viewWhiteList.setOnClickListener(this);
        viewBoostWidget.setOnClickListener(this);
        viewFloatingBooster.setOnClickListener(this);
        viewBatteryPercent.setOnClickListener(this);
        viewFully.setOnClickListener(this);
        viewRate.setOnClickListener(this);
        viewShare.setOnClickListener(this);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setValueCheckBox();
        changeCheckBox();
    }

    private void setValueCheckBox() {
        boolean isAutoBoost = PreferenceUtil.getBoolean(getActivity(),
                PreferenceUtil.AUTO_BOOST, false);
        checkboxAutoBoost.setChecked(isAutoBoost);
        setTextColorAutoBoost(isAutoBoost);

        boolean isFloatingBooster = PreferenceUtil.getBoolean(getActivity(),
                PreferenceUtil.FLOATING_BOOSTER, false);
        checkboxFloating.setChecked(isFloatingBooster);
        setTextColorFloating(isFloatingBooster);

        boolean isBatteryPercent = PreferenceUtil.getBoolean(getActivity(),
                PreferenceUtil.BATTERY_PERCENT, false);
        checkboxBatteryPercent.setChecked(isBatteryPercent);
        setTextColorBatteryPercent(isBatteryPercent);

        boolean isFullyBattery = PreferenceUtil.getBoolean(getActivity(),
                PreferenceUtil.FULLY_BATTERY, false);
        checkboxFullyBattery.setChecked(isFullyBattery);
        setTextColorFullyBattery(isFullyBattery);
    }

    private void changeCheckBox() {
        checkboxAutoBoost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTextColorAutoBoost(isChecked);
                PreferenceUtil.saveBoolean(getActivity(),
                        PreferenceUtil.AUTO_BOOST, isChecked);
                if (isChecked) {
                    startAutoBooster();
                } else {
                    stoptAutoBooster();
                }
            }
        });
        checkboxFloating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTextColorFloating(isChecked);
                PreferenceUtil.saveBoolean(getActivity(),
                        PreferenceUtil.FLOATING_BOOSTER, isChecked);
                if (isChecked) {
                    getActivity().startService(new Intent(getActivity(), ChatHeadService.class));
                } else {
                    getActivity().stopService(new Intent(getActivity(), ChatHeadService.class));
                }
            }
        });
        checkboxBatteryPercent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTextColorBatteryPercent(isChecked);
                PreferenceUtil.saveBoolean(getActivity(),
                        PreferenceUtil.BATTERY_PERCENT, isChecked);
                if (isChecked) {
                    getActivity().startService(new Intent(getActivity(), BatteryService.class));
                } else {
                    getActivity().stopService(new Intent(getActivity(), BatteryService.class));
                }
            }
        });
        checkboxFullyBattery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTextColorFullyBattery(isChecked);
                PreferenceUtil.saveBoolean(getActivity(),
                        PreferenceUtil.FULLY_BATTERY, isChecked);
            }
        });
    }

    private void setTextColorAutoBoost(boolean isChecked) {
        if (isChecked) {
            tvAutoBoost.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.black));
        } else {
            tvAutoBoost.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.grey_500));
        }
    }

    private void setTextColorFloating(boolean isChecked) {
        if (isChecked) {
            tvFloatingBooster.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.black));
        } else {
            tvFloatingBooster.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.grey_500));
        }
    }

    private void setTextColorBatteryPercent(boolean isChecked) {
        if (isChecked) {
            tvBatteryPercent.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.black));
        } else {
            tvBatteryPercent.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.grey_500));
        }
    }

    private void setTextColorFullyBattery(boolean isChecked) {
        if (isChecked) {
            tvFully.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.black));
        } else {
            tvFully.setTextColor(ContextCompat.getColor(getActivity(),
                    R.color.grey_500));
        }
    }

    private void startAutoBooster() {
        getActivity().startService(new Intent(getActivity(), AutoBoostService.class));
    }

    private void stoptAutoBooster() {
        getActivity().stopService(new Intent(getActivity(), AutoBoostService.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewAutoBoost:
                checkboxAutoBoost.performClick();
                break;
            case R.id.viewWhiteList:
                replaceFragment(new WhiteListFragment(), false);
                break;
            case R.id.viewBoostWidget:
                if (SystemClock.elapsedRealtime() - mLastTimeWidget < 1500) {
                    return;
                }
                mLastTimeWidget = SystemClock.elapsedRealtime();
                showHomeWidgetDialog(getActivity());
                break;
            case R.id.viewFloatingBooster:
            	 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     if (!Settings.canDrawOverlays(getActivity())) {
                         Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                 Uri.parse("package:" + getActivity().getPackageName()));
                         startActivity(intent);
                     } else {
                         checkboxFloating.performClick();
                     }
                 } else {
                     checkboxFloating.performClick();
                 }
                 break;
            case R.id.viewBatteryPercent:
                checkboxBatteryPercent.performClick();
                break;
            case R.id.viewFully:
                checkboxFullyBattery.performClick();
                break;
            case R.id.viewRate:
                Utils.rateApp(getActivity());
                break;
            case R.id.viewShare:
                Utils.shareApp(getActivity());
                break;
            default:
                break;
        }
    }

    private void showHomeWidgetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.logo);
        builder.setTitle(getString(R.string.title_home_wiget));
        builder.setMessage(getString(R.string.description_home_wiget));
        builder.setPositiveButton(getString(R.string.got_it), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.action_settings), MainActivity.HeaderBarType.TYPE_CLEAN);
    }
}
