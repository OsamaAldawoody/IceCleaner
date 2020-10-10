package com.phonecleaner.icecleaner.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.adapter.WhiteListAddAdapter;
import com.phonecleaner.icecleaner.asyncTask.ManagerConnect;
import com.phonecleaner.icecleaner.model.Whitelist;
import com.phonecleaner.icecleaner.utils.Utils;

public class AddWhiteListFragment extends BaseFragment {

	private FrameLayout mFrameLayout;
	private RecyclerView mRecyclerViewWhiteList;
	private TextView mTvNoItem;
	private LinearLayout mViewAdd;

	private List<Whitelist> mWhitelists = new ArrayList<>();
	private WhiteListAddAdapter mAdapter;

	private PackageManager mPackageManager;

	private Handler mHandlerLocal = new Handler(Looper.getMainLooper());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPackageManager = getActivity().getPackageManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_whitelist_add,
				container, false);
		mFrameLayout = (FrameLayout) root
				.findViewById(R.id.recyclerViewWhiteList);
		mRecyclerViewWhiteList = new RecyclerView(getActivity());
		mFrameLayout.addView(mRecyclerViewWhiteList);
		mTvNoItem = (TextView) root.findViewById(R.id.tvNoItem);
		mViewAdd = (LinearLayout) root.findViewById(R.id.viewAdd);
		CheckBox checkBoxAddWhiteList = (CheckBox) root
				.findViewById(R.id.checkBoxAddWhiteList);
		checkBoxAddWhiteList
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						for (int i = 0; i < mWhitelists.size(); i++) {
							mWhitelists.get(i).setIsCheck(isChecked);
						}
						mAdapter.notifyDataSetChanged();
					}
				});
		root.findViewById(R.id.btnAddWhiteListAdd).

		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (Whitelist whitelist : mWhitelists) {
					if (whitelist.isCheck()) {
						mPreferenceUtil.addLocked(getActivity(),
								whitelist.getPackageName());
					}
				}
				onBackPressed();
			}
		}

		);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new WhiteListAddAdapter(mWhitelists,
				new WhiteListAddAdapter.OnHandleItemClickListener() {
					@Override
					public void onClickCheckBox(boolean isCheck, int position) {
						mWhitelists.get(position).setIsCheck(!isCheck);
						mAdapter.notifyDataSetChanged();
					}
				});
		mRecyclerViewWhiteList.setLayoutManager(new LinearLayoutManager(
				getActivity()));
		mRecyclerViewWhiteList.setAdapter(mAdapter);

		loadData();
	}

	private void loadData() {
		ManagerConnect managerConnect = new ManagerConnect();
		managerConnect.getListManager(getActivity(),
				new ManagerConnect.OnManagerConnectListener() {
					@Override
					public void OnResultManager(
							final List<ApplicationInfo> result) {
						Runnable runnableLocal = new Runnable() {
							@Override
							public void run() {
								if (result.size() != 0) {
									for (ApplicationInfo applicationInfo : result) {
										if (Utils.isUserApp(applicationInfo)
												&& !Utils
														.checkLockedItem(
																getActivity(),
																applicationInfo.packageName)) {
											mWhitelists
													.add(new Whitelist(
															applicationInfo.packageName,
															applicationInfo
																	.loadLabel(
																			mPackageManager)
																	.toString(),
															applicationInfo
																	.loadIcon(mPackageManager),
															false));
										}
									}
									mAdapter.notifyDataSetChanged();
								}
								if (mWhitelists.size() == 0) {
									mFrameLayout.setVisibility(View.GONE);
									mViewAdd.setVisibility(View.GONE);
									mTvNoItem.setVisibility(View.VISIBLE);
								} else {
									mFrameLayout.setVisibility(View.VISIBLE);
									mViewAdd.setVisibility(View.VISIBLE);
									mTvNoItem.setVisibility(View.GONE);
								}
							}
						};
						mHandlerLocal.postDelayed(runnableLocal, 100);
					}
				}

		);
	}

	@Override
	public void onResume() {
		super.onResume();
		setHeader(getString(R.string.add_to_white_list),
				MainActivity.HeaderBarType.TYPE_CLEAN);
	}
}
