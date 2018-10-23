package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.OrderHistoryFragment;
import com.noqapp.android.client.views.fragments.QueueHistoryFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

public class OrderQueueHistoryActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadTabs loadTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_queue_history);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.history));
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();
    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new QueueHistoryFragment(), "Queue");
        adapter.addFragment(new OrderHistoryFragment(), "Order");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loadTabs) {
            loadTabs.cancel(true);
        }
    }

    private class LoadTabs extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
