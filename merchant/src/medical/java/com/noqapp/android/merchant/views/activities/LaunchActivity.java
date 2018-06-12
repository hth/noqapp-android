package com.noqapp.android.merchant.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.DeviceModel;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.presenter.beans.NavigationBean;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.NavigationDrawerAdapter;
import com.noqapp.android.merchant.views.fragments.AccessDeniedFragment;
import com.noqapp.android.merchant.views.fragments.LoginFragment;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;
import com.noqapp.common.utils.NetworkUtil;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.Serializable;

import io.fabric.sdk.android.Fabric;

public class LaunchActivity extends BaseLaunchActivity {

    private FrameLayout fl_notification;
    private TextView tv_badge;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        Fabric.with(this, new Answers());
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        launchActivity = this;
        DeviceModel.appBlacklistPresenter = this;
        Log.v("device id check", getDeviceID());
        networkUtil = new NetworkUtil(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // to hide the default action bar title
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_badge = (TextView) findViewById(R.id.tv_badge);
        fl_notification = (FrameLayout) findViewById(R.id.fl_notification);
        fl_notification.setVisibility(View.VISIBLE);
        fl_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(launchActivity, NotificationActivity.class);
                startActivity(in);
            }
        });
        if (new AppUtils().isTablet(this)) {
            list_fragment = (FrameLayout) findViewById(R.id.frame_layout);
            list_detail_fragment = (FrameLayout) findViewById(R.id.list_detail_fragment);
        }
        initProgress();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        NavigationBean[] drawerItem = null;
        if (isLoggedIn()) {
            drawerItem = new NavigationBean[4];
            drawerItem[2] = new NavigationBean(R.drawable.profile_red, "Profile");
            drawerItem[3] = new NavigationBean(R.mipmap.logout, "Logout");

        } else {
            drawerItem = new NavigationBean[2];
        }
        drawerItem[0] = new NavigationBean(R.drawable.pie_chart, "Charts");
        drawerItem[1] = new NavigationBean(R.drawable.medical_history, "Medical History");


        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this, R.layout.listitem_navigation_drawer, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedPosition = position;
                switch (selectedPosition) {
                    case 0:
                        if (merchantListFragment.getTopics() != null && merchantListFragment.getTopics().size() > 0) {
                            Intent in1 = new Intent(launchActivity, ChartSampleActivity.class);
                            in1.putExtra("jsonTopic", (Serializable) merchantListFragment.getTopics());
                            startActivity(in1);
                        } else {
                            Toast.makeText(launchActivity, "No queue available", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        Intent in2 = new Intent(launchActivity, MedicalHistoryDetailActivity.class);
                        startActivity(in2);
                        break;
                    case 2:
                        Intent in3 = new Intent(launchActivity, ManagerProfileActivity.class);
                        startActivity(in3);
                        break;
                    case 3:
                        showLogoutDialog();
                        break;
                    default:
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (isLoggedIn()) {
            if (isAccessGrant()) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                if (!new AppUtils().isTablet(getApplicationContext())) {
                    merchantListFragment = new MerchantListFragment();
                    replaceFragmentWithoutBackStack(R.id.frame_layout, merchantListFragment);
                    // setUserName();
                } else {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.3f);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.6f);
                    list_fragment.setLayoutParams(lp1);
                    list_detail_fragment.setLayoutParams(lp2);
                    merchantListFragment = new MerchantListFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, merchantListFragment);
                    //  fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            } else {
                if (new AppUtils().isTablet(getApplicationContext())) {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1.0f);
                    LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.0f);
                    list_fragment.setLayoutParams(lp1);
                    list_detail_fragment.setLayoutParams(lp0);
                }
                AccessDeniedFragment adf = new AccessDeniedFragment();
                replaceFragmentWithoutBackStack(R.id.frame_layout, adf);
            }
            setUserName();
        } else {
            if (new AppUtils().isTablet(getApplicationContext())) {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1.0f);
                LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.0f);
                list_fragment.setLayoutParams(lp1);
                list_detail_fragment.setLayoutParams(lp0);
            }

            replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            DeviceModel.isSupportedAppVersion(UserUtils.getDeviceId());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        int notify_count = NotificationDB.getNotificationCount();
        tv_badge.setText(String.valueOf(notify_count));
        if (notify_count > 0) {
            tv_badge.setVisibility(View.VISIBLE);
        } else {
            tv_badge.setVisibility(View.INVISIBLE);
        }


        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());
    }

}
