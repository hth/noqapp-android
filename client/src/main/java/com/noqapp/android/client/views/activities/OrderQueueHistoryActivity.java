package com.noqapp.android.client.views.activities;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.OrderHistoryFragment;
import com.noqapp.android.client.views.fragments.QueueHistoryFragment;

public class OrderQueueHistoryActivity extends TabbedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        tv_toolbar_title.setText(getString(R.string.history));
    }

    @Override
    protected void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new QueueHistoryFragment(), "Queue");
        adapter.addFragment(new OrderHistoryFragment(), "Order");
        viewPager.setAdapter(adapter);
    }

}
