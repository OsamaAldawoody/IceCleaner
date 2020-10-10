package com.phonecleaner.icecleaner.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ads.control.AdmobHelp;
import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.view.RadarScanView;

public class CleanResultFragment extends BaseFragment {

    private static final String TAG = CleanResultFragment.class.getName();

    private RadarScanView mRadarScanView;
    private ImageView mImageCleanUp;
    private Button mBtnDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_clean_result, container, false);
        mRadarScanView = (RadarScanView) view.findViewById(R.id.radarScanView);
        mImageCleanUp = (ImageView) view.findViewById(R.id.loader);
        mImageCleanUp.setBackgroundResource(R.drawable.loader);
        AnimationDrawable rocketAnimation = (AnimationDrawable) mImageCleanUp.getBackground();
        rocketAnimation.start();
        mBtnDone = (Button) view.findViewById(R.id.btnDone);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
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
        mBtnDone.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                mRadarScanView.setVisibility(View.GONE);
                mBtnDone.setVisibility(View.VISIBLE);
                mImageCleanUp.setBackgroundResource(R.drawable.ic_done);
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.clean_up), MainActivity.HeaderBarType.TYPE_CLEAN_UP);
    }
}
