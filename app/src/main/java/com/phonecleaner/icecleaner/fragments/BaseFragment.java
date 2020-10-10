package com.phonecleaner.icecleaner.fragments;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;


public class BaseFragment extends Fragment {

    protected OnBaseFragmentListener mOnBaseFragmentListener;
    protected PreferenceUtil mPreferenceUtil;
    protected ProgressBar mProgressBarLoading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnBaseFragmentListener = (OnBaseFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBaseFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferenceUtil = new PreferenceUtil();
    }

    /**
     * @param fragment is fragment next
     * @param isBack   is add stack
     */
    protected void replaceFragment(Fragment fragment, boolean isBack) {
        MainActivity act = (MainActivity) getActivity();
        act.replaceFragment(fragment, isBack);
    }

    /**
     * handle event back
     */
    protected void onBackPressed() {
        MainActivity act = (MainActivity) getActivity();
        act.onBack();
    }

    protected void setHeader(String title, MainActivity.HeaderBarType type) {
        if (null != mOnBaseFragmentListener) {
            mOnBaseFragmentListener.setTitleHeader(title);
            mOnBaseFragmentListener.setTypeHeader(type);
        }
    }

    public interface OnBaseFragmentListener {
        void setTitleHeader(String title);

        void setTypeHeader(MainActivity.HeaderBarType type);
    }
}
