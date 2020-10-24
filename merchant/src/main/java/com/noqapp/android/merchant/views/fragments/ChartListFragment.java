package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantStatsApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStat;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.ChartListActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.MerchantChartListAdapter;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;

import java.util.ArrayList;
import java.util.List;

public class ChartListFragment extends BaseFragment implements ChartPresenter {

    public static int selected_pos = 0;
    public ChartFragment chartFragment;
    private MerchantChartListAdapter adapter;
    private Runnable run;
    private ArrayList<JsonTopic> topics = new ArrayList<>();
    private ListView listview;
    private ArrayList<HealthCareStat> healthCareStatList = new ArrayList<>();
    private boolean isFirstTime = true;

    public ChartListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_merchant_chart_list, container, false);
        listview = view.findViewById(R.id.listview);
        MerchantStatsApiCalls merchantStatsApiCalls = new MerchantStatsApiCalls(this);
        Bundle bundle = getArguments();
        run = new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
                listview.invalidateViews();
                listview.refreshDrawableState();
            }
        };
        if (null != bundle) {
            ArrayList<JsonTopic> jsonTopics = (ArrayList<JsonTopic>) bundle.getSerializable("jsonTopic");
            updateListData(jsonTopics);
            initListView();
        }

        if (isFirstTime) {
            if (new NetworkUtil(getActivity()).isOnline()) {
                showProgress();
                setProgressMessage("Getting statistics...");
                merchantStatsApiCalls.healthCare(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
                isFirstTime = false;
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void initListView() {
        listview.setVisibility(View.VISIBLE);
        adapter = new MerchantChartListAdapter(getActivity(), topics);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (LaunchActivity.isTablet) {
                    chartFragment.updateChart(healthCareStatList.get(position));
                    //set page for view pager
                } else {
                    chartFragment = new ChartFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("healthCareStat", healthCareStatList.get(position));
                    chartFragment.setArguments(b);
                    ChartListActivity.getChartListActivity().replaceFragmentWithBackStack(R.id.frame_layout, chartFragment, ChartListFragment.class.getSimpleName());
                }
                selected_pos = position;
                // to set the selected cell color
                getActivity().runOnUiThread(run);
            }
        });

        if (LaunchActivity.isTablet) {
            chartFragment = new ChartFragment();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.list_detail_fragment, chartFragment);
            fragmentTransaction.commit();
        }
    }

    public void updateListData(List<JsonTopic> jsonTopics) {
        topics = new ArrayList<>();
        topics.addAll(jsonTopics);
    }


    @Override
    public void chartError() {
        dismissProgress();
    }

    @Override
    public void chartResponse(HealthCareStatList healthCareStatListTemp) {
        if (null != healthCareStatListTemp) {
            healthCareStatList = new ArrayList<>(healthCareStatListTemp.getHealthCareStat());
            if (LaunchActivity.isTablet) {
                chartFragment.updateChart(healthCareStatList.get(selected_pos));
            }
        }
        dismissProgress();
        Log.v("Chart data", healthCareStatList.toString());
    }
}
