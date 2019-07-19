package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCalls;
import com.noqapp.android.client.presenter.HospitalVisitSchedulePresenter;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.ImmuneAdapter;
import com.noqapp.android.client.views.pojos.ImmuneObjList;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.customviews.CustomToast;

import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalVisitScheduleActivity extends BaseActivity implements HospitalVisitSchedulePresenter {

    private ArrayList<ImmuneObjList> temp = new ArrayList<>();
    private RecyclerView rcv_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunization);
        initActionsViews(true);
        tv_toolbar_title.setText("Immunization History");
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
        Map<String, ArrayList<JsonHospitalVisitSchedule>> listHashMap = new HashMap<>();
        for (int i = 0; i < jsonHospitalVisitSchedules.size(); i++) {
            JsonHospitalVisitSchedule jsonHospitalVisitSchedule = jsonHospitalVisitSchedules.get(i);
            ArrayList<JsonHospitalVisitSchedule> value = listHashMap.get(jsonHospitalVisitSchedule.getHeader());
            if (value != null) {
                listHashMap.get(jsonHospitalVisitSchedule.getHeader()).add(jsonHospitalVisitSchedule);
            } else {
                // Key might be present...
                if (listHashMap.containsKey(jsonHospitalVisitSchedule.getHeader())) {
                    listHashMap.put(jsonHospitalVisitSchedule.getHeader(), new ArrayList<JsonHospitalVisitSchedule>());
                    listHashMap.get(jsonHospitalVisitSchedule.getHeader()).add(jsonHospitalVisitSchedule);
                } else {
                    // Definitely no such key
                    listHashMap.put(jsonHospitalVisitSchedule.getHeader(), new ArrayList<JsonHospitalVisitSchedule>());
                    listHashMap.get(jsonHospitalVisitSchedule.getHeader()).add(jsonHospitalVisitSchedule);
                }
            }
        }
        for (Map.Entry<String, ArrayList<JsonHospitalVisitSchedule>> entry : listHashMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            ImmuneObjList aa = new ImmuneObjList();
            aa.setHeaderTitle(entry.getKey());
            aa.setImmuneObjs(entry.getValue());
            temp.add(aa);
        }
        ImmuneAdapter immuneAdapter = new ImmuneAdapter(this, temp,
                null);
        rcv_header.setAdapter(immuneAdapter);
    }
}
