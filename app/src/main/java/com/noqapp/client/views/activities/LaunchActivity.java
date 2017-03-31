package com.noqapp.client.views.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.noqapp.client.R;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.QueuePresenter;
import com.noqapp.client.views.adapters.ViewPagerAdapter;
import com.noqapp.client.views.fragments.ListQueueFragment;
import com.noqapp.client.views.fragments.MeFragment;
import com.noqapp.client.views.fragments.ScanQueueFragment;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LaunchActivity extends FragmentActivity  {


    @BindView(R.id.viewPager)
    public  ViewPager viewPager;
//    @BindView(R.id.pagerHeader)
//    protected PagerTabStrip tabStrip;
     public static  ViewPager tempViewpager ;
    public final static String KEY_SHOWFRAGMENT =  "show_fragment";
    public final static String fragment_queueList = ListQueueFragment.class.getSimpleName();
    public final static String fragment_Me = MeFragment.class.getSimpleName();
    public final static String fragment_scan = ScanQueueFragment.class.getSimpleName();


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
        viewPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager(),titleTabs);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
        tempViewpager = viewPager;

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null)
//        {
//            String fragmentname = bundle.getString(KEY_SHOWFRAGMENT);
//
//            if (fragmentname.equalsIgnoreCase(fragment_queueList))
//            {
//                viewPager.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        viewPager.setCurrentItem(1);
//                    }
//                }, 100);
//               // viewPager.setCurrentItem(1);
//
//            }
//
//        }
    }


}
