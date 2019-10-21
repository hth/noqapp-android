package com.noqapp.android.client.views.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.QuestionTypeEnum;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.SurveyFragment;

import java.util.HashMap;

public class SurveyViewPager extends BaseActivity {
    private ViewPager viewPager;
    private LoadTabs loadTabs;
    private HashMap<String, QuestionTypeEnum> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_survey_pager);
        initActionsViews(false);
        viewPager = findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        temp = (HashMap<String, QuestionTypeEnum>)getIntent().getSerializableExtra("map");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadTabs = new LoadTabs();
                loadTabs.execute();
            }
        }, 100);


    }


    private class LoadTabs extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                setupViewPager();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupViewPager() {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        if(null != temp && temp.size() >0){
            int i = 0;
            for (HashMap.Entry<String, QuestionTypeEnum> entry : temp.entrySet()) {
                String key = entry.getKey();
                QuestionTypeEnum value = entry.getValue();
                SurveyFragment surveyFragment = new SurveyFragment();
                Bundle b = new Bundle();
                b.putString("question",key);
                b.putSerializable("q_type",value);
                surveyFragment.setArguments(b);
                adapter.addFragment(surveyFragment, "FRAG" + i);
                i++;
            }

        }
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

    }


}
