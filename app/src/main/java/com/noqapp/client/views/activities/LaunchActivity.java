package com.noqapp.client.views.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import com.noqapp.client.R;
import com.noqapp.client.views.adapters.ViewPagerAdapter;
import com.noqapp.client.views.fragments.ListQueueFragment;
import com.noqapp.client.views.fragments.MeFragment;
import com.noqapp.client.views.fragments.ScanQueueFragment;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchActivity extends FragmentActivity {


    @BindView(R.id.viewPager)
    public ViewPager viewPager;
    //    @BindView(R.id.pagerHeader)
//    protected PagerTabStrip tabStrip;
    public static ViewPager tempViewpager;
    public final static String KEY_SHOWFRAGMENT = "show_fragment";
    public final static String fragment_queueList = ListQueueFragment.class.getSimpleName();
    public final static String fragment_Me = MeFragment.class.getSimpleName();
    public final static String fragment_scan = ScanQueueFragment.class.getSimpleName();


    private static final String TAG = LaunchActivity.class.getSimpleName();
    public static final String DID = UUID.randomUUID().toString();
    private Button btnScanner;
    private ViewPagerAdapter viewPagerAdapter;
    private String[] titleTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        titleTabs = this.getResources().getStringArray(R.array.title_main_viewpager_tab);
        viewPagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), titleTabs);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerAdapter);
        tempViewpager = viewPager;
    }


}
