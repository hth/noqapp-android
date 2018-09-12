package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.fragments.InviteFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class InviteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setVisibility(View.VISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_invite));
        Bundle b = new Bundle();
        b.putString("invite_code", NoQueueBaseActivity.getInviteCode());
        InviteFragment inf = new InviteFragment();
        inf.setArguments(b);

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_invite, inf, "").commit();
    }

}
