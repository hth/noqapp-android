package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.fragments.MerchantChartListFragment;

import java.util.ArrayList;


public class ChartSampleActivity extends AppCompatActivity  {

    private FrameLayout fl_notification;
    private TextView tv_toolbar_title;
    private ImageView actionbarBack;
    private MerchantChartListFragment merchantChartListFragment;
    private FrameLayout list_fragment, list_detail_fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_sample);


        fl_notification = (FrameLayout) findViewById(R.id.fl_notification);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Charts");
        merchantChartListFragment = new MerchantChartListFragment();
        Bundle b = new Bundle();
        b.putSerializable("jsonTopic", (ArrayList<JsonTopic>) getIntent().getExtras().getSerializable("jsonTopic"));
        merchantChartListFragment.setArguments(b);

        if (!new AppUtils().isTablet(getApplicationContext())) {

            replaceFragmentWithoutBackStack(R.id.frame_layout, merchantChartListFragment);
            // setUserName();
        } else {

            list_fragment = (FrameLayout) findViewById(R.id.frame_layout);
            list_detail_fragment = (FrameLayout) findViewById(R.id.list_detail_fragment);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.3f);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.6f);
            list_fragment.setLayoutParams(lp1);
            list_detail_fragment.setLayoutParams(lp2);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, merchantChartListFragment);
            //  fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }




    }


    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }
}
