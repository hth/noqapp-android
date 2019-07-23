package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.noqapp.android.common.pojos.DisplayNotification;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.NotificationListAdapter;

import java.util.List;


public class NotificationActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ListView listview = findViewById(R.id.listview);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        RelativeLayout rl_empty = findViewById(R.id.rl_empty);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_notification));
        List<DisplayNotification> notificationsList = NotificationDB.getNotificationsList();
        NotificationListAdapter adapter = new NotificationListAdapter(NotificationActivity.this, notificationsList);
        listview.setAdapter(adapter);
        if(notificationsList.size()<=0){
            listview.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        }else{
            listview.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // mark all the entry as read
        NotificationDB.updateNotification();

    }
}
