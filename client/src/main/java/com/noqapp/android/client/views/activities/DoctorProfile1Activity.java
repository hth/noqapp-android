package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * // one -> http://www.tutorialsface.com/2017/09/profile-activity-layout-android-sample-example-tutorial/
 */

public class DoctorProfile1Activity extends AppCompatActivity {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile_one);
        ButterKnife.bind(this);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setVisibility(View.VISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Doctor Profile");
    }

}
