package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.database.utils.ReviewDB;
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
    @BindView(R.id.iv_notification)
    protected ImageView iv_notification;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        iv_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_notification));
        List<NotificationBeans> NotificationsList = ReviewDB.getNotificationsList();
        NotificationListAdapter adapter = new NotificationListAdapter(NotificationActivity.this, NotificationsList);
        listview.setAdapter(adapter);
    }
}
