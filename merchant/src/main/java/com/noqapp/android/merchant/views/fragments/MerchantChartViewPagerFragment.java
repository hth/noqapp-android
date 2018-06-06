package com.noqapp.android.merchant.views.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantStatsModel;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStat;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStatList;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.ChartViewPagerAdapter;
import com.noqapp.android.merchant.views.customsviews.CustomViewPager;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;

import java.util.ArrayList;
import java.util.List;

public class MerchantChartViewPagerFragment extends Fragment implements ChartPresenter {

    public static int pagercurrrentpos = 0;
    private static int pos = 0;
    public ChartViewPagerAdapter adapter;
    private CustomViewPager viewPager;
    private ImageView leftNav, rightNav;
    private ArrayList<JsonTopic> topicsList;
    private ArrayList<HealthCareStat> healthCareStat = new ArrayList<>();
    private static UpdateListColorCallBack updateListColorCallBack;

    public interface UpdateListColorCallBack {
        void onUpdateListColorCallBack(int pos);
    }

    public static void setUpdateListColorCallBack(UpdateListColorCallBack updateListColorBack) {
        updateListColorCallBack = updateListColorBack;
    }
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchantviewpager, container, false);
        viewPager = (CustomViewPager) view.findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        Bundle bundle = getArguments();
        if (null != bundle) {
            topicsList = (ArrayList<JsonTopic>) bundle.getSerializable("jsonMerchant");
            pagercurrrentpos = pos = bundle.getInt("position");
        }
        adapter = new ChartViewPagerAdapter(getActivity(),healthCareStat);
        initProgress();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);
        leftNav = (ImageView) view.findViewById(R.id.left_nav);
        rightNav = (ImageView) view.findViewById(R.id.right_nav);

        if (topicsList.size() > 1) {
            leftNav.setVisibility(View.VISIBLE);
            rightNav.setVisibility(View.VISIBLE);
            if (pos == 0) {
                leftNav.setVisibility(View.INVISIBLE);
            } else if (pos == topicsList.size() - 1) {
                rightNav.setVisibility(View.INVISIBLE);
            }
        } else {
            leftNav.setVisibility(View.INVISIBLE);
            rightNav.setVisibility(View.INVISIBLE);
        }
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                }
                if (tab > 0) {
                    leftNav.setVisibility(View.VISIBLE);
                } else if (tab == 0) {
                    leftNav.setVisibility(View.INVISIBLE);
                }
                if (viewPager.getAdapter().getCount() > 1)
                    rightNav.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(tab);
                // to update the selected color of merchant list
                updateListColorCallBack.onUpdateListColorCallBack(tab);
            }
        });

        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab < viewPager.getAdapter().getCount() - 1) {
                    tab++;
                }
                if (tab < viewPager.getAdapter().getCount() - 1) {
                    rightNav.setVisibility(View.VISIBLE);
                } else if (tab == viewPager.getAdapter().getCount() - 1) {
                    rightNav.setVisibility(View.INVISIBLE);
                }
                if (viewPager.getAdapter().getCount() > 1)
                    leftNav.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(tab);
                // to update the selected color of merchant list
                updateListColorCallBack.onUpdateListColorCallBack(tab);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                pagercurrrentpos = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int num) {

            }
        });
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            MerchantStatsModel.chartPresenter = this;
            MerchantStatsModel.doctor(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue_chart_detail));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        if (new AppUtils().isTablet(getActivity())) {
            LaunchActivity.getLaunchActivity().enableDisableBack(false);
        } else {
            LaunchActivity.getLaunchActivity().enableDisableBack(true);
        }
    }

    public void updateListData(final ArrayList<JsonTopic> jsonTopics) {
        try {
            topicsList = jsonTopics;
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPage(int pos) {
        viewPager.setCurrentItem(pos);
        if(pos == 0){
            leftNav.setVisibility(View.INVISIBLE);
            rightNav.setVisibility(View.VISIBLE);
        }else if(pos == topicsList.size()-1){
            leftNav.setVisibility(View.VISIBLE);
            rightNav.setVisibility(View.INVISIBLE);
        }else{
            leftNav.setVisibility(View.VISIBLE);
            rightNav.setVisibility(View.VISIBLE);
        }
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    @Override
    public void chartError() {
        dismissProgress();
    }

    @Override
    public void chartResponse(HealthCareStatList healthCareStatList) {
        if (null != healthCareStatList) {
            adapter = new ChartViewPagerAdapter(getActivity(), healthCareStatList.getHealthCareStat());
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(pos);
        }
        dismissProgress();
        Log.v("Chart data",healthCareStatList.toString());
    }
}
