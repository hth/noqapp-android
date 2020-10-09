package com.noqapp.android.client.views.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.noqapp.android.client.R;

public abstract class TabbedActivity extends BaseActivity {
    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    protected LoadTabs loadTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        initActionsViews(false);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();
    }

    protected abstract void setupViewPager(ViewPager viewPager);

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
