package com.phonecleaner.icecleaner.fragments;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.control.AdmobHelp;
import com.phonecleaner.icecleaner.GuideActivity;
import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.asyncTask.TotalMemoryStorageTask;
import com.phonecleaner.icecleaner.asyncTask.TotalRamTask;
import com.phonecleaner.icecleaner.utils.ScreenUtil;
import com.phonecleaner.icecleaner.view.ArcProgress;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1337;
    private ArcProgress mArcProgressMemory;
    private ArcProgress mArcProgressRam;

    private TextView mTvStorage;

    private int mSizeProgressMemory;
    private int mSizeProgressRam;

    private Handler mTimerHandler = null;
    TotalMemoryStorageTask mTotalMemoryStorageTask;
    TotalRamTask mTotalRamTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSizeProgressMemory = ScreenUtil.getWidthScreen(getActivity()) / 2;
        mSizeProgressRam = ScreenUtil.getWidthScreen(getActivity()) / 4;
        mTimerHandler = new Handler();
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mArcProgressMemory = (ArcProgress) view.findViewById(R.id.arcProgressMemory);
        mArcProgressRam = (ArcProgress) view.findViewById(R.id.arcProgressRam);
        LinearLayout viewClean = (LinearLayout) view.findViewById(R.id.viewClean);
        LinearLayout viewBoost = (LinearLayout) view.findViewById(R.id.viewBoost);
        LinearLayout viewAppManager = (LinearLayout) view.findViewById(R.id.viewAppManager);
        LinearLayout viewBattery = (LinearLayout) view.findViewById(R.id.viewBattery);

        mTvStorage = (TextView) view.findViewById(R.id.tvStorage);

        mArcProgressMemory.getLayoutParams().width = mSizeProgressMemory;
        mArcProgressMemory.getLayoutParams().height = mSizeProgressMemory;
        mArcProgressRam.getLayoutParams().width = mSizeProgressRam;
        mArcProgressRam.getLayoutParams().height = mSizeProgressRam;

        viewClean.setOnClickListener(this);
        viewBoost.setOnClickListener(this);
        viewAppManager.setOnClickListener(this);
        viewBattery.setOnClickListener(this);
        return view;
    }

    /**
     * Runnable interface for update current memory status
     */
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
        	mTotalMemoryStorageTask = new TotalMemoryStorageTask(getActivity(), mArcProgressMemory, mTvStorage);
        	mTotalMemoryStorageTask.execute();
        	mTotalRamTask = new TotalRamTask(getActivity(), mArcProgressRam);
        	mTotalRamTask.execute();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewClean:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!canAccessLocation()) {
                        requestPermissions();
                    } else {
                        replaceFragment(new CleanFragment(), false);
                    }
                } else {
                    replaceFragment(new CleanFragment(), false);
                }
                break;
            case R.id.viewBoost:
                boostDevice();

                break;
            case R.id.viewAppManager:
                AdmobHelp.getInstance().showInterstitialAd(new AdmobHelp.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        replaceFragment(new AppManagerFragment(), false);
                    }
                });

                break;
            case R.id.viewBattery:
                replaceFragment(new BatteryFragment(), false);
                break;
            default:
                break;
        }
    }
    void boostDevice(){
        if (!isUsageAccessAllowed()) {
            startActivity(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS));
            startActivity(new Intent(getActivity(), GuideActivity.class));


        }else{
            replaceFragment(new BoostFragment(), false);
        }
    }
    public final boolean isUsageAccessAllowed() {
        boolean granted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                AppOpsManager appOps = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getActivity().getPackageName());
                if (mode == AppOpsManager.MODE_DEFAULT) {
                    String permissionUsage = "android.permission.PACKAGE_USAGE_STATS";
                    granted = (getActivity().checkCallingOrSelfPermission(permissionUsage) == PackageManager.PERMISSION_GRANTED);
                } else {
                    granted = (mode == AppOpsManager.MODE_ALLOWED);
                }
            } catch (Throwable e) {
            }
        } else {
            return true;
        }
        return granted;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimerHandler.removeCallbacks(timerRunnable);// remove timer runnable interface
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.app_name), MainActivity.HeaderBarType.TYPE_HOME);
        mTimerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerHandler.removeCallbacks(timerRunnable);// remove timer runnable interface
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (PackageManager.PERMISSION_GRANTED == getActivity().checkSelfPermission(permission));
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().requestPermissions(LOCATION_PERMS, WRITE_EXTERNAL_STORAGE_REQUEST);
        }
    }
}
