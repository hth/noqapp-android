package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCalls;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.HospitalVisitScheduleFragment;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.HospitalVisitForEnum;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HospitalVisitScheduleActivity extends TabbedActivity implements
        HospitalVisitSchedulePresenter {
    private RelativeLayout rl_empty;
    private LinearLayout ll_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_toolbar_title.setText("Hospital Visit Schedule");
        rl_empty = findViewById(R.id.rl_empty);
        ll_data = findViewById(R.id.ll_data);
        MedicalProfile medicalProfile = (MedicalProfile) getIntent().getSerializableExtra("medicalProfile");
        UserMedicalProfileApiCalls userMedicalProfileApiCalls = new UserMedicalProfileApiCalls();
        userMedicalProfileApiCalls.setHospitalVisitSchedulePresenter(this);
        if (NetworkUtils.isConnectingToInternet(this)) {
            if (UserUtils.isLogin()) {
                userMedicalProfileApiCalls.hospitalVisitSchedule(UserUtils.getEmail(), UserUtils.getAuth(), medicalProfile);
                setProgressMessage("fetching immunization history...");
                showProgress();
            } else {
                new CustomToast().showToast(this, "Please login to see the details");
            }
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }


    @Override
    protected void setupViewPager(ViewPager viewPager) {
    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList) {
        dismissProgress();
        Log.e("immunization", jsonHospitalVisitScheduleList.toString());
        List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = jsonHospitalVisitScheduleList.getJsonHospitalVisitSchedules();

        List<JsonHospitalVisitSchedule> immunizationList = new ArrayList<>();
        List<JsonHospitalVisitSchedule> vaccinationList = new ArrayList<>();
        for (int i = 0; i < jsonHospitalVisitSchedules.size(); i++) {
            if (jsonHospitalVisitSchedules.get(i).getHospitalVisitFor() == HospitalVisitForEnum.IMU) {
                immunizationList.add(jsonHospitalVisitSchedules.get(i));
            } else {
                vaccinationList.add(jsonHospitalVisitSchedules.get(i));
            }
        }
        HospitalVisitScheduleFragment hvsfVaccine = new HospitalVisitScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) vaccinationList);
        hvsfVaccine.setArguments(bundle);

        HospitalVisitScheduleFragment hvsfImmune = new HospitalVisitScheduleFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) immunizationList);
        hvsfImmune.setArguments(b);
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        if (vaccinationList.size() > 0)
            adapter.addFragment(hvsfVaccine, "Vaccination");
        if (immunizationList.size() > 0)
            adapter.addFragment(hvsfImmune, "Immunization");
        viewPager.setAdapter(adapter);
        if (immunizationList.size() <= 0 && vaccinationList.size() <= 0) {
            ll_data.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            ll_data.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        dismissProgress();
        // do nothing
    }
}
