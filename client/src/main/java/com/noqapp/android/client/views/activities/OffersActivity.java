package com.noqapp.android.client.views.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.AllOffersFragment;
import com.noqapp.android.client.views.fragments.MyOffersFragment;

public class OffersActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadTabs loadTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.offers));
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();
    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllOffersFragment(), "New Offers");
        if (UserUtils.isLogin()) {
            adapter.addFragment(new MyOffersFragment(), "My Offers");
        }
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
