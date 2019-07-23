package com.noqapp.android.merchant.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.model.types.medical.HospitalVisitForEnum;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.HospitalVisitScheduleFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HospitalVisitScheduleActivity extends BaseActivity implements
        HospitalVisitSchedulePresenter {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String qUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunization);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(v -> finish());
        tv_toolbar_title.setText("Hospital Visit Schedule");
        String codeQR = getIntent().getStringExtra("qCodeQR");
        JsonQueuedPerson jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        qUserId = jsonQueuedPerson.getQueueUserId();
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            MedicalHistoryApiCalls medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
            medicalHistoryApiCalls.hospitalVisitSchedule(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), new FindMedicalProfile().setCodeQR(codeQR)
                            .setQueueUserId(jsonQueuedPerson.getQueueUserId()));
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

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
        bundle.putString("qUserId",qUserId);
        hvsfVaccine.setArguments(bundle);

        HospitalVisitScheduleFragment hvsfImmune = new HospitalVisitScheduleFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) immunizationList);
        b.putString("qUserId",qUserId);
        hvsfImmune.setArguments(b);
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        if (vaccinationList.size() > 0)
            adapter.addFragment(hvsfVaccine, "Vaccination");
        if (immunizationList.size() > 0)
            adapter.addFragment(hvsfImmune, "Immunization");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        dismissProgress();
        // Do nothing
    }


}
