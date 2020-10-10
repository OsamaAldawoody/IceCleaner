package com.phonecleaner.icecleaner.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.adapter.AnimatedExpandableListView;
import com.phonecleaner.icecleaner.adapter.AppManagerAdapter;
import com.phonecleaner.icecleaner.asyncTask.ManagerConnect;
import com.phonecleaner.icecleaner.model.GroupItemAppManager;
import com.phonecleaner.icecleaner.utils.Utils;

import static android.app.Activity.RESULT_OK;

public class AppManagerFragment extends BaseFragment {

    private static final String TAG = AppManagerFragment.class.getName();
    private static final int UNINSTALL_REQUEST_CODE = 1;
    private AnimatedExpandableListView mRecyclerView;
    private int mPositionUninstall;
    private List<GroupItemAppManager> mGroupItems = new ArrayList<>();
    private AppManagerAdapter mAdapter;

    private Handler mHandlerLocal = new Handler(Looper.getMainLooper());

    private int mGroupPosition;
    private int mChildPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_app_manager, container, false);
        mRecyclerView = (AnimatedExpandableListView) view.findViewById(R.id.recyclerView);
        mProgressBarLoading = (ProgressBar) view.findViewById(R.id.progressBarLoading);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
        loadData();
    }

    private void initAdapter() {
        mAdapter = new AppManagerAdapter(getActivity(), mGroupItems, new AppManagerAdapter.OnClickItemListener() {
            @Override
            public void onUninstallApp(int groupPosition, int childPosition) {
                mGroupPosition = groupPosition;
                mChildPosition = childPosition;
                mPositionUninstall = mChildPosition;
                ApplicationInfo app = mGroupItems.get(groupPosition).getItems().get(childPosition);
                Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                intent.setData(Uri.parse("package:" + app.packageName));
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
            }

            @Override
            public void onClickItem(int groupPosition, int childPosition) {
                if (mGroupItems.get(groupPosition).getItems().get(childPosition).packageName != null) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:"
                            + mGroupItems.get(groupPosition).getItems().get(childPosition).packageName));

                    getActivity().startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData() {
        mProgressBarLoading.setVisibility(View.VISIBLE);
        ManagerConnect managerConnect = new ManagerConnect();
        managerConnect.getListManager(getActivity(), new ManagerConnect.OnManagerConnectListener() {
            @Override
            public void OnResultManager(final List<ApplicationInfo> result) {
                Runnable runnableLocal = new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "====---->>" + result.size());
                        mProgressBarLoading.setVisibility(View.GONE);
                        if (result.size() != 0) {
                            //set app user
                            GroupItemAppManager groupItemAppManagerUser = new GroupItemAppManager();
                            groupItemAppManagerUser.setTitle(getString(R.string.third_party_apps));
                            groupItemAppManagerUser.setType(GroupItemAppManager.TYPE_USER_APPS);
                            int countUser = 0;
                            List<ApplicationInfo> applicationInfosUser = new ArrayList<>();
                            for (ApplicationInfo applicationInfo : result) {
                                if (!applicationInfo.packageName.equals(getActivity().getPackageName())
                                        && Utils.isUserApp(applicationInfo)) {
                                    countUser++;
                                    applicationInfosUser.add(applicationInfo);
                                }
                            }
                            groupItemAppManagerUser.setItems(applicationInfosUser);
                            groupItemAppManagerUser.setTotal(countUser);
                            mGroupItems.add(groupItemAppManagerUser);
                            //set app system
                            GroupItemAppManager groupItemAppManagerSystem = new GroupItemAppManager();
                            groupItemAppManagerSystem.setTitle(getString(R.string.system_apps));
                            groupItemAppManagerSystem.setType(GroupItemAppManager.TYPE_SYSTEM_APPS);
                            int countSystem = 0;
                            List<ApplicationInfo> applicationInfosSystem = new ArrayList<>();
                            for (ApplicationInfo applicationInfo : result) {
                                if (!Utils.isUserApp(applicationInfo)) {
                                    countSystem++;
                                    applicationInfosSystem.add(applicationInfo);
                                }
                            }
                            groupItemAppManagerSystem.setItems(applicationInfosSystem);
                            groupItemAppManagerSystem.setTotal(countSystem);
                            mGroupItems.add(groupItemAppManagerSystem);
                            mAdapter.notifyDataSetChanged();
                            if (mRecyclerView.isGroupExpanded(0)) {
                                mRecyclerView.collapseGroupWithAnimation(0);
                            } else {
                                mRecyclerView.expandGroupWithAnimation(0);
                            }
                        }
                    }
                };
                mHandlerLocal.postDelayed(runnableLocal, 100);
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mGroupItems.get(mGroupPosition).getItems().remove(mChildPosition);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader(getString(R.string.app_manager), MainActivity.HeaderBarType.TYPE_CLEAN);
    }
}
