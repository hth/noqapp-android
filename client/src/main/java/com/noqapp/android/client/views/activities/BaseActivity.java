package com.noqapp.android.client.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;


public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog progressDialog;
    protected ImageView iv_home;
    protected ImageView actionbarBack;
    protected TextView tv_toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initProgress();

    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("fetching data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    protected void initActionsViews(boolean isHomeVisible) {
        iv_home = findViewById(R.id.iv_home);
        actionbarBack = findViewById(R.id.actionbarBack);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        iv_home.setVisibility(isHomeVisible ? View.VISIBLE : View.GONE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToA = new Intent(BaseActivity.this, LaunchActivity.class);
                goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goToA);
            }
        });
    }
}
