package com.phonecleaner.icecleaner.fragments;

import java.util.ArrayList;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.asyncTask.TaskBoost;
import com.phonecleaner.icecleaner.model.TaskInfo;
import com.phonecleaner.icecleaner.view.DonutProgress;

public class BoostResultFragment extends BaseFragment {

    private static final String KEY_RESULT_BOOST = "boost";

    private View mView;
    private TextView mTvBoostDone;
    private ImageView mImgBooster;
    private ImageView mImgBoostDone;
    private Button mBtnDone;
    private DonutProgress mProgressBarBoost;
    private ArrayList<TaskInfo> mTaskInfos = new ArrayList<>();

    public static BoostResultFragment newInstance(ArrayList<TaskInfo> taskInfos) {
        BoostResultFragment f = new BoostResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_RESULT_BOOST, taskInfos);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskInfos = getArguments().getParcelableArrayList(KEY_RESULT_BOOST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.fragment_boost_result, container, false);

        mImgBooster = (ImageView) mView.findViewById(R.id.imgBooster);
        mImgBoostDone = (ImageView) mView.findViewById(R.id.imgBoostDone);

        mTvBoostDone = (TextView) mView.findViewById(R.id.tvBoostDone);
        mBtnDone = (Button) mView.findViewById(R.id.btnDone);
        mProgressBarBoost = (DonutProgress) mView.findViewById(R.id.progressBarBoost);

        mImgBooster.setBackgroundResource(R.drawable.loader_boost);
        AnimationDrawable rocketAnimation = (AnimationDrawable) mImgBooster.getBackground();
        rocketAnimation.start();
        slideToTopSlow(mImgBooster);

        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                onBackPressed();
            }
        });
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImgBoostDone.setVisibility(View.GONE);
        mBtnDone.setVisibility(View.INVISIBLE);
        mTvBoostDone.setVisibility(View.INVISIBLE);
    }

    // To animate view slide out from bottom to top
    public void slideToTopSlow(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 50 / 2, 0, -50);
        animate.setDuration(2000);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // no op
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new TaskBoost(getActivity(), mProgressBarBoost, mTaskInfos,
                        new TaskBoost.OnTaskBoostListener() {
                            @Override
                            public void OnResult() {
                                slideToTop(mImgBooster);
                            }
                        }).execute();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // no op
            }
        });
        view.startAnimation(animate);
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, mView.getHeight() / 2, 0, -mView.getHeight());
        animate.setDuration(1000);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // no op
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImgBoostDone.setVisibility(View.VISIBLE);
                mBtnDone.setVisibility(View.VISIBLE);
                mTvBoostDone.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // no op
            }
        });
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.clean_up), MainActivity.HeaderBarType.TYPE_CLEAN_UP);
    }
}
