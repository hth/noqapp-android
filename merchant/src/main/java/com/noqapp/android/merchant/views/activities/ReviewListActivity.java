package com.noqapp.android.merchant.views.activities;


import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.fragments.ReviewListFragment;


public class ReviewListActivity extends BaseActivity {
    public static ReviewListActivity getReviewListActivity() {
        return reviewListActivity;
    }

    private static ReviewListActivity reviewListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        reviewListActivity = this;
        initActionsViews(false);
        tv_toolbar_title.setText("Reviews");
        ReviewListFragment reviewListFragment = new ReviewListFragment();
        Bundle b = new Bundle();
        b.putSerializable("jsonTopic", getIntent().getExtras().getSerializable("jsonTopic"));
        reviewListFragment.setArguments(b);

        if (LaunchActivity.isTablet) {
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
