package com.noqapp.android.merchant.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.ViewPagerAdapter;

import java.util.ArrayList;


public class MerchantViewPagerFragment extends Fragment {


    public static int pagercurrrentpos = 0;
    private static int pos = 0;
    public ViewPagerAdapter adapter;
    private ViewPager viewPager;
    private ImageView leftNav, rightNav;
    private ArrayList<JsonTopic> topicsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchantviewpager, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        Bundle bundle = getArguments();
        if (null != bundle) {
            topicsList = (ArrayList<JsonTopic>) bundle.getSerializable("jsonMerchant");
            pagercurrrentpos = pos = bundle.getInt("position");

        }
        adapter = new ViewPagerAdapter(getActivity(), topicsList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);
        leftNav = (ImageView) view.findViewById(R.id.left_nav);
        rightNav = (ImageView) view.findViewById(R.id.right_nav);


        if (topicsList.size() > 1) {
            leftNav.setVisibility(View.VISIBLE);
            rightNav.setVisibility(View.VISIBLE);
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
                    viewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPager.setCurrentItem(tab);
                }
            }
        });

        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab < viewPager.getAdapter().getCount()) {
                    tab++;
                    viewPager.setCurrentItem(tab);
                }
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
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_queue_detail));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }


    public void updateListData(ArrayList<JsonTopic> jsonTopics) {
        topicsList = jsonTopics;
        adapter.notifyDataSetChanged();
    }
}
