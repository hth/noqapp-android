package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.noqapp.android.common.pojos.MenuDrawer;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

public class LaunchActivity extends BaseLaunchActivity {
    private NoQueueMessagingService noQueueMessagingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        Fabric.with(this, new Answers());
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        launchActivity = this;
        Log.v("Device id check", getDeviceID());
        networkUtil = new NetworkUtil(this);
        noQueueMessagingService = new NoQueueMessagingService(this);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // to hide the default action bar title
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        tv_name = findViewById(R.id.tv_name);
        TextView tv_badge = findViewById(R.id.tv_badge);
        tv_badge.setVisibility(View.GONE);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        fl_notification.setVisibility(View.GONE);
        if (isTablet) {
            list_fragment = findViewById(R.id.frame_layout);
            list_detail_fragment = findViewById(R.id.list_detail_fragment);
        }
        initDrawer();
        //enableDisableDrawer(false);
        toolbar.setVisibility(View.GONE);
        getSupportActionBar().hide();
        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            deviceApiCalls.isSupportedAppVersion(UserUtils.getDeviceId());
        }
        tv_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tv, 0);
        tv_name.setOnClickListener(v -> {
            Intent in = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(in);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void updateMenuList(boolean showChart) {
        super.updateMenuList(showChart);
        try {
            menuDrawerItems.add(3, new MenuDrawer("Marquees Settings", true, false, R.drawable.ic_add));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callMarqueeSettings() {
        super.callMarqueeSettings();
        Intent in = new Intent(this, MarqueeActivity.class);
        startActivity(in);
    }
}
