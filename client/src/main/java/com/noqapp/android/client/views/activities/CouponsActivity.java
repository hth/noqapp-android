package com.noqapp.android.client.views.activities;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.AllCouponsFragment;
import com.noqapp.android.client.views.fragments.MyCouponsFragment;

public class CouponsActivity extends TabbedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_toolbar_title.setText(getString(R.string.offers));
    }

    @Override
    protected void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllCouponsFragment(), "New Coupons");
        if (UserUtils.isLogin()) {
            adapter.addFragment(new MyCouponsFragment(), "My Coupons");
        }
        viewPager.setAdapter(adapter);
    }
}
