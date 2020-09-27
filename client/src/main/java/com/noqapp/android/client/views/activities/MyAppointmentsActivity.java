package com.noqapp.android.client.views.activities;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.PastAppointmentFragment;
import com.noqapp.android.client.views.fragments.UpcomingAppointmentFragment;

public class MyAppointmentsActivity extends TabbedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(MyApplication.isLockMode);
        super.onCreate(savedInstanceState);
        tv_toolbar_title.setText(getString(R.string.my_appointments));
    }

    @Override
    protected void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UpcomingAppointmentFragment(), "Upcoming");
        adapter.addFragment(new PastAppointmentFragment(), "Past");
        viewPager.setAdapter(adapter);
    }
}
