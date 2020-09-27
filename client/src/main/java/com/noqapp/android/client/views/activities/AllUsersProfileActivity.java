package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.ProfileAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.List;

public class AllUsersProfileActivity extends BaseActivity implements ProfileAdapter.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_profile);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.medical_profiles));
        RecyclerView rv_user_profile = findViewById(R.id.rv_user_profile);

        rv_user_profile.setHasFixedSize(true);
        rv_user_profile.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_user_profile.setItemAnimator(new DefaultItemAnimator());
        if (UserUtils.isLogin()) {
            List<JsonProfile> profileList = AppInitialize.getUserProfile().getDependents();
            profileList.add(0, AppInitialize.getUserProfile());
            ProfileAdapter profileAdapter = new ProfileAdapter(this, profileList, this);
            rv_user_profile.setAdapter(profileAdapter);
        } else {
            new CustomToast().showToast(this, "Please login to see the details");
        }
    }


    @Override
    public void onProfileItemClick(JsonProfile jsonProfile) {
        MedicalProfile medicalProfile = new MedicalProfile();
        medicalProfile.setMedicalProfileOfQueueUserId(jsonProfile.getQueueUserId());
        if (AppInitialize.getUserProfile().getQueueUserId().equalsIgnoreCase(jsonProfile.getQueueUserId())) {
            medicalProfile.setGuardianQueueUserId("");
        } else {
            medicalProfile.setGuardianQueueUserId(AppInitialize.getUserProfile().getQueueUserId());
        }
        Intent in = new Intent(this, MedicalProfileActivity.class);
        in.putExtra("medicalProfile", medicalProfile);
        in.putExtra("jsonProfile", jsonProfile);
        startActivity(in);
    }

    @Override
    public void onHospitalVisitClick(JsonProfile jsonProfile) {
        MedicalProfile medicalProfile = new MedicalProfile();
        medicalProfile.setMedicalProfileOfQueueUserId(jsonProfile.getQueueUserId());
        if (AppInitialize.getUserProfile().getQueueUserId().equalsIgnoreCase(jsonProfile.getQueueUserId())) {
            medicalProfile.setGuardianQueueUserId("");
        } else {
            medicalProfile.setGuardianQueueUserId(AppInitialize.getUserProfile().getQueueUserId());
        }
        Intent in = new Intent(this, HospitalVisitScheduleActivity.class);
        in.putExtra("medicalProfile", medicalProfile);
        in.putExtra("jsonProfile", jsonProfile);
        startActivity(in);
    }
}
