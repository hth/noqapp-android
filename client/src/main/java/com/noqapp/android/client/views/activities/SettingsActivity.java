package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.action_settings));
    }

    @Override
    public void onClick(View v) {

    }

}
