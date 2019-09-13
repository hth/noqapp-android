package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.fragments.ChartListFragment;


public class ChartListActivity extends BaseActivity {

    public static ChartListActivity getChartListActivity() {
        return chartListActivity;
    }

    private static ChartListActivity chartListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LaunchActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        chartListActivity = this;
        initActionsViews(false);
        tv_toolbar_title.setText("Statistics");
        ChartListFragment chartListFragment = new ChartListFragment();
        Bundle b = new Bundle();
        b.putSerializable("jsonTopic", getIntent().getExtras().getSerializable("jsonTopic"));
        chartListFragment.setArguments(b);

        if (LaunchActivity.isTablet) {
            FrameLayout list_fragment = findViewById(R.id.frame_layout);
            FrameLayout list_detail_fragment = findViewById(R.id.list_detail_fragment);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.7f);
            list_fragment.setLayoutParams(lp1);
            list_detail_fragment.setLayoutParams(lp2);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, chartListFragment);
            fragmentTransaction.commit();
        } else {
            replaceFragmentWithoutBackStack(R.id.frame_layout, chartListFragment);
        }

    }

}
