package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.noqapp.android.common.beans.NavigationBean;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;


public class LaunchActivity extends BaseLaunchActivity {

    private TextView tv_badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        Fabric.with(this, new Answers());
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        launchActivity = this;
        Log.v("device id check", getDeviceID());
        networkUtil = new NetworkUtil(this);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // to hide the default action bar title
        tv_name = findViewById(R.id.tv_name);
        tv_badge = findViewById(R.id.tv_badge);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        fl_notification.setVisibility(View.VISIBLE);
        fl_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(launchActivity, NotificationActivity.class);
                startActivity(in);
            }
        });
        if (new AppUtils().isTablet(this)) {
            list_fragment = findViewById(R.id.frame_layout);
            list_detail_fragment = findViewById(R.id.list_detail_fragment);
        }
        initProgress();
        initDrawer();

        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            deviceApiCalls.isSupportedAppVersion(UserUtils.getDeviceId());
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

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());
    }

    @Override
    public void updateMenuList(boolean showChart) {
        super.updateMenuList(showChart);
        try {
            if (launchActivity.getUserProfile().getUserLevel() == UserLevelEnum.S_MANAGER) {
                drawerItem.add(2, new NavigationBean(R.drawable.case_history, getString(R.string.menu_preference)));
                drawerItem.add(3, new NavigationBean(R.drawable.pharmacy, getString(R.string.menu_pref_store)));
                if(!AppUtils.isRelease()) {
                    // Currently supported only in debug mode
                    drawerItem.add(4, new NavigationBean(R.drawable.appointment, getString(R.string.menu_appointments)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callPreference() {
        super.callPreference();
        Intent intentPreference = new Intent(launchActivity, PreferenceActivity.class);
        startActivity(intentPreference);
    }

    @Override
    public void callPreferredStore() {
        super.callPreferredStore();
        Intent intentPreference = new Intent(launchActivity, PreferredStoreActivity.class);
        startActivity(intentPreference);
    }

    @Override
    public void callAppointments() {
        super.callAppointments();
        Intent intentAppointments = new Intent(launchActivity, AppointmentActivity.class);
        startActivity(intentAppointments);
    }
}
