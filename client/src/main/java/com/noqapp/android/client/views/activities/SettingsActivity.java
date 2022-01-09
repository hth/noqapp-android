package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.view.View;

import com.noqapp.android.client.R;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoqApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.action_settings));
    }

    @Override
    public void onClick(View v) {

    }
}
