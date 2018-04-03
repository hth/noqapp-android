package com.noqapp.android.client.views.activities;

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

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.presenter.beans.NotificationBeans;
import com.noqapp.android.client.views.adapters.NotificationListAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity  {

    @BindView(R.id.listview)
    protected ListView listview;
    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_empty)
    protected TextView tv_empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setVisibility(View.VISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_notification));
        List<NotificationBeans> notificationsList = NotificationDB.getNotificationsList();
        NotificationListAdapter adapter = new NotificationListAdapter(this, notificationsList);
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
