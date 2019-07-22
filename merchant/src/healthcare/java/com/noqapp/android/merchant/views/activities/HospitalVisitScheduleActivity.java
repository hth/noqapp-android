package com.noqapp.android.merchant.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.HospitalVisitScheduleAdapter;

import java.util.ArrayList;
import java.util.List;

public class HospitalVisitScheduleActivity extends BaseActivity implements HospitalVisitSchedulePresenter {
    private ArrayList<JsonHospitalVisitSchedule> temp = new ArrayList<>();
    private RecyclerView rcv_header;

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
        rcv_header = findViewById(R.id.rcv_header);
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());
        HospitalVisitScheduleAdapter hospitalVisitScheduleAdapter = new HospitalVisitScheduleAdapter(this, temp, null);
        rcv_header.setAdapter(hospitalVisitScheduleAdapter);
        JsonQueuedPerson jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        MedicalHistoryApiCalls medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        medicalHistoryApiCalls.hospitalVisitSchedule(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), new FindMedicalProfile().setCodeQR(codeQR)
                        .setQueueUserId(jsonQueuedPerson.getQueueUserId()));


    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList) {
        Log.e("immunization", jsonHospitalVisitScheduleList.toString());
        List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = jsonHospitalVisitScheduleList.getJsonHospitalVisitSchedules();
        HospitalVisitScheduleAdapter hospitalVisitScheduleAdapter = new HospitalVisitScheduleAdapter(this, jsonHospitalVisitSchedules,
                null);
        rcv_header.setAdapter(hospitalVisitScheduleAdapter);
    }
}
