package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.UserUtils;

import com.crashlytics.android.answers.Answers;

import net.danlew.android.joda.JodaTimeAndroid;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import io.fabric.sdk.android.Fabric;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        tv_name = findViewById(R.id.tv_name);
        tv_badge = findViewById(R.id.tv_badge);
        tv_badge.setVisibility(View.GONE);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        fl_notification.setVisibility(View.GONE);
        if (new AppUtils().isTablet(this)) {
            list_fragment = findViewById(R.id.frame_layout);
            list_detail_fragment = findViewById(R.id.list_detail_fragment);
        }
        initProgress();
        initDrawer();
        //enableDisableDrawer(false);
        toolbar.setVisibility(View.GONE);
        getSupportActionBar().hide();
        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            deviceModel.isSupportedAppVersion(UserUtils.getDeviceId());
        }

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LaunchActivity.this,MainActivity.class);
                startActivity(in);

//                for (int i = 0; i < merchantListFragment.getTopics().size(); i++) {
//                    JsonTopic jsonTopic = merchantListFragment.getTopics().get(i);
//                    String start = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getStartHour());
//                    String end = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getEndHour());
//                    Log.e("Queue Validity--"," start_time "+start+" end_time "+end+" current time validity is: "+isCurrentTimeInRange(start,end));
//                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private boolean isCurrentTimeInRange(String start, String end) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm",Locale.getDefault());
            Date date = new Date();
            String current = formatter.format(date);

            Date time1 = new SimpleDateFormat("HH:mm",Locale.getDefault()).parse(start);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            Date time2 = new SimpleDateFormat("HH:mm",Locale.getDefault()).parse(end);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            Date d = new SimpleDateFormat("HH:mm",Locale.getDefault()).parse(current);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            return x.after(calendar1.getTime()) && x.before(calendar2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
