package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCalls;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.HospitalVisitScheduleAdapter;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;

import java.util.List;

public class HospitalVisitScheduleActivity extends BaseActivity implements
        HospitalVisitSchedulePresenter {
    private RecyclerView rcv_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunization);
        initActionsViews(true);
        tv_toolbar_title.setText("Hospital Visit Schedule");
        rcv_header = findViewById(R.id.rcv_header);
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());
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
    public void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList) {
        Log.e("immunization", jsonHospitalVisitScheduleList.toString());
        List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = jsonHospitalVisitScheduleList.getJsonHospitalVisitSchedules();
        HospitalVisitScheduleAdapter hospitalVisitScheduleAdapter = new HospitalVisitScheduleAdapter(this, jsonHospitalVisitSchedules,
                null);
        rcv_header.setAdapter(hospitalVisitScheduleAdapter);
    }
}
