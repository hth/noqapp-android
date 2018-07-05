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
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.fragments.ChartListFragment;


public class ChartListActivity extends AppCompatActivity {

    private FrameLayout fl_notification;
    private TextView tv_toolbar_title;
    private ImageView actionbarBack;
    private ChartListFragment merchantChartListFragment;
    private FrameLayout list_fragment, list_detail_fragment;

    public static ChartListActivity getChartListActivity() {
        return chartListActivity;
    }

    private static ChartListActivity chartListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_sample);
        chartListActivity = this;

        fl_notification = findViewById(R.id.fl_notification);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Charts");
        merchantChartListFragment = new ChartListFragment();
        Bundle b = new Bundle();
        b.putSerializable("jsonTopic", getIntent().getExtras().getSerializable("jsonTopic"));
        merchantChartListFragment.setArguments(b);

        if (!new AppUtils().isTablet(getApplicationContext())) {
            replaceFragmentWithoutBackStack(R.id.frame_layout, merchantChartListFragment);
        } else {

            list_fragment = findViewById(R.id.frame_layout);
            list_detail_fragment = findViewById(R.id.list_detail_fragment);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.3f);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.6f);
            list_fragment.setLayoutParams(lp1);
            list_detail_fragment.setLayoutParams(lp2);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, merchantChartListFragment);
            fragmentTransaction.commit();
        }


    }


    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    public void replaceFragmentWithBackStack(int container, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment, tag).addToBackStack(tag).commit();
    }
}
