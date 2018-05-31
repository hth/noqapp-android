package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.views.adapters.NotificationListAdapter;
import com.noqapp.library.beans.NotificationBeans;

import java.util.List;


public class NotificationActivity extends AppCompatActivity  {



    private FrameLayout fl_notification;
    private ListView listview;
    private TextView tv_empty;
    private TextView tv_toolbar_title;
    private ImageView actionbarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        listview = (ListView) findViewById(R.id.listview);
        fl_notification =(FrameLayout) findViewById(R.id.fl_notification);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_notification));
        List<NotificationBeans> notificationsList = NotificationDB.getNotificationsList();
        NotificationListAdapter adapter = new NotificationListAdapter(NotificationActivity.this, notificationsList);
        listview.setAdapter(adapter);
        if(notificationsList.size()<=0){
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        }else{
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // mark all the entry as read
        NotificationDB.updateNotification();

    }
}
