package com.noqapp.android.merchant.views.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantStatsModel;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStat;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.MerchantChartListAdapter;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;

import java.util.ArrayList;
import java.util.List;

public class MerchantChartListFragment extends Fragment implements  ChartPresenter{

    public static int selected_pos = 0;
    public ChartFragment chartFragment;
    private MerchantChartListAdapter adapter;
    private Runnable  run;
    private ArrayList<JsonTopic> topics = new ArrayList<>();
    private ListView listview;
    private ArrayList<HealthCareStat> healthCareStatList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private MerchantStatsModel merchantStatsModel;
    public MerchantChartListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_chart_list, container, false);
        listview = view.findViewById(R.id.listview);
        initProgress();
        merchantStatsModel = new MerchantStatsModel(this);
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


        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            merchantStatsModel.doctor(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
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
                if (!new AppUtils().isTablet(getActivity())) {
                    chartFragment = new ChartFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("healthCareStatList", healthCareStatList);
                    b.putInt("position", position);
                    chartFragment.setArguments(b);
                    LaunchActivity.getLaunchActivity().replaceFragmentWithBackStack(R.id.frame_layout, chartFragment, "MerchantViewPagerFragment");
                } else {
                    chartFragment.setPage(position, healthCareStatList);
                    //set page for view pager
                }
                selected_pos = position;
                // to set the selected cell color
                getActivity().runOnUiThread(run);
            }
        });

        if (new AppUtils().isTablet(getActivity())) {
            chartFragment = new ChartFragment();
            Bundle b = new Bundle();
            b.putSerializable("jsonMerchant", topics);
            b.putInt("position", selected_pos);
            chartFragment.setArguments(b);
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
            healthCareStatList= new ArrayList<>(healthCareStatListTemp.getHealthCareStat());
            chartFragment.setPage(selected_pos,healthCareStatList);
        }
        dismissProgress();
        Log.v("Chart data",healthCareStatList.toString());
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("fetching data...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
