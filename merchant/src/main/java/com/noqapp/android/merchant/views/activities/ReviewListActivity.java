package com.noqapp.android.merchant.views.activities;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.fragments.ReviewListFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class ReviewListActivity extends AppCompatActivity {


    public static ReviewListActivity getReviewListActivity() {
        return reviewListActivity;
    }

    private static ReviewListActivity reviewListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        reviewListActivity = this;

        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText("Reviews");
        ReviewListFragment reviewListFragment = new ReviewListFragment();
        Bundle b = new Bundle();
        b.putSerializable("jsonTopic", getIntent().getExtras().getSerializable("jsonTopic"));
        reviewListFragment.setArguments(b);

        if (new AppUtils().isTablet(getApplicationContext())) {
            FrameLayout list_fragment = findViewById(R.id.frame_layout);
            FrameLayout list_detail_fragment = findViewById(R.id.list_detail_fragment);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.7f);
            list_fragment.setLayoutParams(lp1);
            list_detail_fragment.setLayoutParams(lp2);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, reviewListFragment);
            fragmentTransaction.commit();
        } else {
            replaceFragmentWithoutBackStack(R.id.frame_layout, reviewListFragment);
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
