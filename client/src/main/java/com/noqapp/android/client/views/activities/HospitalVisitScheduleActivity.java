package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.viewpager.widget.ViewPager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_toolbar_title.setText("Hospital Visit Schedule");
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
    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        dismissProgress();
        // do nothing
    }
}
