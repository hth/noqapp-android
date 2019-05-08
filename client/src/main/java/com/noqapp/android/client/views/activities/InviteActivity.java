package com.noqapp.android.client.views.activities;

import android.os.Bundle;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.fragments.InviteFragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class InviteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        initActionsViews(true);
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
