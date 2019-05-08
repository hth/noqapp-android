package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.body.UserMedicalProfile;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.ProfileAdapter;
import com.noqapp.android.common.beans.JsonProfile;

import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllUsersProfileActivity extends BaseActivity implements ProfileAdapter.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_profile);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.medical_profiles));
        RecyclerView rv_user_profile = findViewById(R.id.rv_user_profile);

        rv_user_profile.setHasFixedSize(true);
        rv_user_profile.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_user_profile.setItemAnimator(new DefaultItemAnimator());
        if (UserUtils.isLogin()) {
            List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
            profileList.add(0, NoQueueBaseActivity.getUserProfile());
            ProfileAdapter profileAdapter = new ProfileAdapter(this, profileList, this);
            rv_user_profile.setAdapter(profileAdapter);
        } else {
            Toast.makeText(this, "Please login to see the details", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onProfileItemClick(int pos, JsonProfile jsonProfile) {
        UserMedicalProfile userMedicalProfile = new UserMedicalProfile();
        userMedicalProfile.setMedicalProfileOfQueueUserId(jsonProfile.getQueueUserId());
        if (LaunchActivity.getUserProfile().getQueueUserId().equalsIgnoreCase(jsonProfile.getQueueUserId())) {
            userMedicalProfile.setGuardianQueueUserId("");
        } else {
            userMedicalProfile.setGuardianQueueUserId(LaunchActivity.getUserProfile().getQueueUserId());
        }
        Intent in = new Intent(this, MedicalProfileActivity.class);
        in.putExtra("userMedicalProfile", userMedicalProfile);
        in.putExtra("jsonProfile",jsonProfile);
        startActivity(in);
    }
}
