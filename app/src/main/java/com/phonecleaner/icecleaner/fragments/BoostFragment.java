package com.phonecleaner.icecleaner.fragments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.control.AdmobHelp;
import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.adapter.BoostAdapter;
import com.phonecleaner.icecleaner.asyncTask.TaskList;
import com.phonecleaner.icecleaner.model.TaskInfo;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;
import com.phonecleaner.icecleaner.utils.Utils;
import com.phonecleaner.icecleaner.view.WaveLoadingView;


public class BoostFragment extends BaseFragment {

    private static final String TAG = BoostFragment.class.getName();

    private TextView mTvTotalBoost;
    private TextView mTvType;
    private TextView mTvSuggesterBoost;
    private TextView mTvBoosterPercent;
    private FrameLayout mFrameLayout;
    private RecyclerView mRecyclerViewBoost;
    private WaveLoadingView mWaveLoadingView;

    private LinearLayout mViewEmpty;

    private Button mBtnBoost;
    private Button mBtnDone;

    private BoostAdapter mAdapter;
    private List<TaskInfo> mTaskInfos = new ArrayList<>();

    private long mTotalSelect;
    private long mTotalMemory;
    private long mAvailMem;

    private Handler mTimerHandler = null;
    private boolean mIsFragmentPause;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_boost, container, false);

        mTvTotalBoost = (TextView) root.findViewById(R.id.tvTotalBoost);
        mTvType = (TextView) root.findViewById(R.id.tvType);
        mTvSuggesterBoost = (TextView) root.findViewById(R.id.tvSuggesterBoost);
        mFrameLayout = (FrameLayout) root.findViewById(R.id.recyclerViewBoost);
        mRecyclerViewBoost = new RecyclerView(getActivity());
        mFrameLayout.addView(mRecyclerViewBoost);
        mProgressBarLoading = (ProgressBar) root.findViewById(R.id.progressBarLoading);
        mTvBoosterPercent = (TextView) root.findViewById(R.id.tvBoosterPercent);
        mBtnBoost = (Button) root.findViewById(R.id.btnBoost);
        mViewEmpty = (LinearLayout) root.findViewById(R.id.viewEmpty);
        mWaveLoadingView = (WaveLoadingView) root.findViewById(R.id.waveLoadingView);

        mBtnBoost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.saveLongTimeBoost(getActivity(),System.currentTimeMillis());
                replaceFragment(BoostResultFragment.newInstance((ArrayList<TaskInfo>) mTaskInfos), false);
            }
        });
        mBtnDone = (Button) root.findViewById(R.id.btnDone);
        mBtnDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmobHelp.getInstance().showInterstitialAd(new AdmobHelp.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        onBackPressed();
                    }
                });


            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTotalMemory = getTotalMemory();// get total device memory
        updateMemoryStatus();// update device memory status
        mTimerHandler = new Handler();// initialize timer handler

        //set show/hide view
        mBtnDone.setVisibility(View.GONE);
        mBtnBoost.setVisibility(View.GONE);

        mAdapter = new BoostAdapter(getActivity(), mTaskInfos, new BoostAdapter.OnHandleItemBoostClickListener() {
            @Override
            public void onSelectedItem(int position) {
                boolean ischeck = mTaskInfos.get(position).isChceked();
                if (ischeck) {
                    mTotalSelect = mTotalSelect - mTaskInfos.get(position).getMem();
                } else {
                    mTotalSelect = mTotalSelect + mTaskInfos.get(position).getMem();
                }
                Utils.setTextFromSize(mTotalSelect, mTvTotalBoost, mTvType);
                mTaskInfos.get(position).setChceked(!ischeck);
                mAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerViewBoost.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerViewBoost.setAdapter(mAdapter);

        new TaskList(getActivity(), mProgressBarLoading, new TaskList.OnTaskListListener() {
            @Override
            public void OnResult(ArrayList<TaskInfo> taskInfos, long total) {
                if (mIsFragmentPause) {
                    return;
                }
                long useRam = mTotalMemory - mAvailMem;
                int percentMem = (int) (((double) useRam / (double) mTotalMemory) * 100);
                mWaveLoadingView.setProgressValue(100 - percentMem);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mTvTotalBoost.setText(String.valueOf(percentMem));
                    mTvType.setText(getString(R.string.percent_none));
                    mTvSuggesterBoost.setText("Used");
                    if (taskInfos.size() != 0) {

                        if((PreferenceUtil.getLongTimeBoost(getActivity())+120*1000)<System.currentTimeMillis()){
                            mViewEmpty.setVisibility(View.GONE);
                            mFrameLayout.setVisibility(View.VISIBLE);
                            mBtnDone.setVisibility(View.GONE);
                            mBtnBoost.setVisibility(View.VISIBLE);
                            mTvSuggesterBoost.setVisibility(View.VISIBLE);

                            mTaskInfos.addAll(taskInfos);
                            mAdapter.notifyDataSetChanged();
//                            mTotalSelect = total;
                        }else{
                            mViewEmpty.setVisibility(View.VISIBLE);
                            mFrameLayout.setVisibility(View.GONE);
                            mBtnDone.setVisibility(View.VISIBLE);
                            mBtnBoost.setVisibility(View.GONE);
                        }
                    }else{
                        mViewEmpty.setVisibility(View.VISIBLE);
                        mFrameLayout.setVisibility(View.GONE);
                        mBtnDone.setVisibility(View.VISIBLE);
                        mBtnBoost.setVisibility(View.GONE);

                        mTvTotalBoost.setText(String.valueOf(percentMem));
                        mTvType.setText(getString(R.string.percent_none));
                    }



                }else{
                    if (taskInfos.size() != 0) {
                        Log.e("CHECKTIME","TIME "+(PreferenceUtil.getLongTimeBoost(getActivity())+120*1000));
                        Log.e("CHECKTIME","TIME "+System.currentTimeMillis());
                        if(PreferenceUtil.getLongTimeBoost(getActivity())+120*1000<System.currentTimeMillis()){
                            mViewEmpty.setVisibility(View.GONE);
                            mFrameLayout.setVisibility(View.VISIBLE);
                            mBtnDone.setVisibility(View.GONE);
                            mBtnBoost.setVisibility(View.VISIBLE);
                            mTvSuggesterBoost.setVisibility(View.VISIBLE);

                            mTaskInfos.addAll(taskInfos);
                            mAdapter.notifyDataSetChanged();
                            mTotalSelect = total;
                            Utils.setTextFromSize(total, mTvTotalBoost, mTvType);
                        }else{
                            mViewEmpty.setVisibility(View.VISIBLE);
                            mFrameLayout.setVisibility(View.GONE);
                            mBtnDone.setVisibility(View.VISIBLE);
                            mBtnBoost.setVisibility(View.GONE);

                            mTvTotalBoost.setText(String.valueOf(percentMem));
                            mTvType.setText(getString(R.string.percent_none));
                            mTvSuggesterBoost.setText("Used");
                        }

                    } else {
                        mViewEmpty.setVisibility(View.VISIBLE);
                        mFrameLayout.setVisibility(View.GONE);
                        mBtnDone.setVisibility(View.VISIBLE);
                        mBtnBoost.setVisibility(View.GONE);

                        mTvTotalBoost.setText(String.valueOf(percentMem));
                        mTvType.setText(getString(R.string.percent_none));
                        mTvSuggesterBoost.setText("Used");
                    }
                }
            }
        }).execute();
    }

    /**
     * Runnable interface for update current memory status
     */
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            updateMemoryStatus();
            mTimerHandler.postDelayed(timerRunnable, 5000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.phone_boots), MainActivity.HeaderBarType.TYPE_CLEAN);
        mTimerHandler.postDelayed(timerRunnable, 0);
        mIsFragmentPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsFragmentPause = true;
        mTimerHandler.removeCallbacks(timerRunnable);// remove timer runnable interface
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerHandler.removeCallbacks(timerRunnable);// remove timer runnable interface
    }

    private void updateMemoryStatus() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) getActivity().getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryInfo(memInfo);

        mAvailMem = memInfo.availMem;
        float f1 = mTotalMemory;
        int i = (int) (((float) mAvailMem / f1) * 100F);
        if (i != 0) {
            mTvBoosterPercent.setText(String.format(getString(R.string.index_storage),
                    Utils.formatSize(mTotalMemory - mAvailMem), Utils.formatSize(mTotalMemory)));
        }
    }

    private long getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2 = "tag";
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            for (int i = 0; i < 2; i++) {
                str2 = str2 + " " + localBufferedReader.readLine();
            }
            arrayOfString = str2.split("\\s+");
            // total Memory
            initial_memory = Integer.valueOf(arrayOfString[2]);
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (initial_memory * 1024L);
    }
}
