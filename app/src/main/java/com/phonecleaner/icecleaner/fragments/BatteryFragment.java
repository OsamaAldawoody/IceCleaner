package com.phonecleaner.icecleaner.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.adapter.BatterySaverAdapter;
import com.phonecleaner.icecleaner.model.BatterySaver;
import com.phonecleaner.icecleaner.utils.Utils;

public class BatteryFragment extends BaseFragment {

	private static final String TAG = BatteryFragment.class.getName();

	private ProgressBar mProgressBarBattery;
	private TextView mTvPercentage;

	private FrameLayout mFrameLayout;
	private RecyclerView mRecyclerViewBattery;

	private List<BatterySaver> mBatterySavers = new ArrayList<>();
	private BatterySaverAdapter mAdapter;

	private int mPositionExpand = -1;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			float percentage = level / (float) scale;
			int mProgressBattery = (int) ((percentage) * 100);
			if (mProgressBattery > 100) {
				mProgressBattery = 100;
			}
			mTvPercentage.setText(String.valueOf(mProgressBattery));
			mProgressBarBattery.setProgress(mProgressBattery);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_battery, container,
				false);
		mProgressBarBattery = (ProgressBar) view
				.findViewById(R.id.progressBarBattery);
		mTvPercentage = (TextView) view.findViewById(R.id.tvPercentage);
		mFrameLayout = (FrameLayout) view
				.findViewById(R.id.recyclerViewBattery);
		mRecyclerViewBattery = new RecyclerView(getActivity());
		mFrameLayout.addView(mRecyclerViewBattery);
		Button mBtnAddmode = (Button) view.findViewById(R.id.btnAddmode);
		mBtnAddmode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPositionExpand != -1) {
					mBatterySavers.get(mPositionExpand).setExpand(true);
				}
				replaceFragment(BatteryFragmentDetails.newInstance(0,
						BatterySaver.TYPE_ADD,
						(ArrayList<BatterySaver>) mBatterySavers), false);
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		getActivity().registerReceiver(mBroadcastReceiver, iFilter);
		mRecyclerViewBattery.setLayoutManager(new LinearLayoutManager(
				getActivity()));
		mAdapter = new BatterySaverAdapter(getActivity(), mBatterySavers,
				new BatterySaverAdapter.OnHandleItemClickListener() {
					@Override
					public void onClickItemSaver(int position) {
						mPositionExpand = position;
						for (int i = 0; i < mBatterySavers.size(); i++) {
							if (i != position) {
								mBatterySavers.get(i).setExpand(true);
							} else {
								if (mBatterySavers.get(i).isExpand()) {
									mBatterySavers.get(i).setExpand(false);
								} else {
									mBatterySavers.get(i).setExpand(true);
								}
							}
						}
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onClickItemDescription(int position) {
						if (mPositionExpand != -1) {
							mBatterySavers.get(mPositionExpand).setExpand(true);
						}
						replaceFragment(BatteryFragmentDetails.newInstance(
								position, mBatterySavers.get(position)
										.getType(),
								(ArrayList<BatterySaver>) mBatterySavers),
								false);
					}

					@Override
					public void onSelectedItem(int position) {
						Utils.setBatterySaverSelected(getActivity(),
								mBatterySavers.get(position));
						mPreferenceUtil.removeAll(getActivity());
						for (int i = 0; i < mBatterySavers.size(); i++) {
							if (i != position) {
								mBatterySavers.get(i).setSelected(false);
							} else {
								mBatterySavers.get(i).setSelected(true);
							}
							mBatterySavers.get(i).setExpand(true);
						}
						mPreferenceUtil.saveBatterySaver(getActivity(),
								mBatterySavers);
						mAdapter.notifyDataSetChanged();
						onBackPressed();
					}
				});
		mRecyclerViewBattery.setAdapter(mAdapter);
		if (mBatterySavers.size() == 0) {
			if (mPreferenceUtil.getListBatterySaver(getActivity()) != null) {
				mBatterySavers.addAll(mPreferenceUtil
						.getListBatterySaver(getActivity()));
				Log.i(TAG, "" + mBatterySavers);
				mAdapter.notifyDataSetChanged();
			} else {
				mBatterySavers.add(new BatterySaver(true,
						getString(R.string.super_saving), true, false, 0,
						15000, false, false, false, false, false,
						BatterySaver.TYPE_SUPPER_SAVING));
				mBatterySavers.add(new BatterySaver(false,
						getString(R.string.normal), true, true, 30, 60000,
						true, false, true, false, false,
						BatterySaver.TYPE_NORMAL));
				mBatterySavers.add(new BatterySaver(false,
						getString(R.string.custom), true, false, 0, 15000,
						false, false, false, false, false,
						BatterySaver.TYPE_CUSTOM));
				mPreferenceUtil.saveBatterySaver(getActivity(), mBatterySavers);
				mAdapter.notifyDataSetChanged();
			}
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setHeader(getString(R.string.battery_saver),
				MainActivity.HeaderBarType.TYPE_BATTERY_PLAN);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}
}
