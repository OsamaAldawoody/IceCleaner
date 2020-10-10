package com.phonecleaner.icecleaner.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.adapter.WhiteListAdapter;
import com.phonecleaner.icecleaner.model.Whitelist;
import com.phonecleaner.icecleaner.utils.PreferenceUtil;

public class WhiteListFragment extends BaseFragment {

	private FrameLayout mFrameLayout;
	private RecyclerView mRecyclerViewWhiteList;
	private TextView mTvNoItem;

	private List<Whitelist> mWhitelists = new ArrayList<>();
	private WhiteListAdapter mAdapter;

	private PackageManager mPackageManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPackageManager = getActivity().getPackageManager();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_whitelist, container,
				false);
		mFrameLayout = (FrameLayout) root
				.findViewById(R.id.recyclerViewWhiteList);
		mRecyclerViewWhiteList = new RecyclerView(getActivity());
		mFrameLayout.addView(mRecyclerViewWhiteList);
		mTvNoItem = (TextView) root.findViewById(R.id.tvNoItem);
		root.findViewById(R.id.btnAddWhiteList).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						replaceFragment(new AddWhiteListFragment(), false);
					}
				});
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new WhiteListAdapter(mWhitelists,
				new WhiteListAdapter.OnHandleItemClickListener() {
					@Override
					public void onClickRemove(int position) {
						mPreferenceUtil.removeLocked(getActivity(), mWhitelists
								.get(position).getPackageName());
						mWhitelists.remove(position);
						mAdapter.notifyDataSetChanged();
						if (mWhitelists.size() == 0) {
							mTvNoItem.setVisibility(View.VISIBLE);
							mRecyclerViewWhiteList.setVisibility(View.GONE);
						} else {
							mTvNoItem.setVisibility(View.GONE);
							mRecyclerViewWhiteList.setVisibility(View.VISIBLE);
						}
					}
				});
		mRecyclerViewWhiteList.setLayoutManager(new LinearLayoutManager(
				getActivity()));
		mRecyclerViewWhiteList.setAdapter(mAdapter);

		loadData();
	}

	private void loadData() {
		PreferenceUtil preferenceUtil = new PreferenceUtil();
		List<String> whitelistSaves = preferenceUtil.getLocked(getActivity());
		if (whitelistSaves == null || whitelistSaves.size() == 0) {
			mTvNoItem.setVisibility(View.VISIBLE);
			mFrameLayout.setVisibility(View.GONE);
		} else {
			mTvNoItem.setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			for (String packageName : whitelistSaves) {
				try {
					ApplicationInfo applicationInfo = mPackageManager
							.getApplicationInfo(packageName, 0);
					mWhitelists.add(new Whitelist(packageName, applicationInfo
							.loadLabel(mPackageManager).toString(),
							applicationInfo.loadIcon(mPackageManager), false));
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mWhitelists.clear();
	}

	@Override
	public void onResume() {
		super.onResume();
		setHeader(getString(R.string.white_list),
				MainActivity.HeaderBarType.TYPE_CLEAN);
	}
}
