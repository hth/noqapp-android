package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.utils.UserUtils;

import net.danlew.android.joda.JodaTimeAndroid;

public class LaunchActivity extends BaseLaunchActivity {
    private FrameLayout fl_notification;
    private TextView tv_badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        launchActivity = this;
        Log.v("device id check", AppInitialize.getDeviceID());
        networkUtil = new NetworkUtil(this);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // to hide the default action bar title
        tv_name = findViewById(R.id.tv_name);
        tv_badge = findViewById(R.id.tv_badge);
        fl_notification = findViewById(R.id.fl_notification);
        fl_notification.setVisibility(View.VISIBLE);
        fl_notification.setOnClickListener(view -> {
            Intent in = new Intent(launchActivity, NotificationActivity.class);
            startActivity(in);
        });
        if (isTablet) {
            list_fragment = findViewById(R.id.frame_layout);
            list_detail_fragment = findViewById(R.id.list_detail_fragment);
        }
        initDrawer();

        /* Call to check if the current version of app blacklist or old. */
        if (new NetworkUtil(this).isOnline()) {
            deviceApiCalls.isSupportedAppVersion();
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

}
