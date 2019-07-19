package com.noqapp.android.client.views.activities;


import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCalls;
import com.noqapp.android.client.presenter.ImmunizationHistoryPresenter;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.ImmuneAdapter;
import com.noqapp.android.client.views.pojos.ImmuneObjList;
import com.noqapp.android.common.beans.medical.JsonImmunization;
import com.noqapp.android.common.beans.medical.JsonImmunizationList;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImmunizationActivity extends BaseActivity implements ImmunizationHistoryPresenter {

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
        userMedicalProfileApiCalls.setImmunizationHistoryPresenter(this);
        if (NetworkUtils.isConnectingToInternet(this)) {
            if (UserUtils.isLogin()) {
                userMedicalProfileApiCalls.immunizationHistory(UserUtils.getEmail(), UserUtils.getAuth(), medicalProfile);
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
    public void immunizationHistoryResponse(JsonImmunizationList jsonImmunizationList) {
        Log.e("immunization",jsonImmunizationList.toString());
        List<JsonImmunization> jsonImmunizations = jsonImmunizationList.getJsonImmunizations();
        Map<String,ArrayList<JsonImmunization>> listHashMap = new HashMap<>();
        for (int i = 0; i < jsonImmunizations.size(); i++) {
            JsonImmunization jsonImmunization = jsonImmunizations.get(i);
            ArrayList<JsonImmunization> value = listHashMap.get(jsonImmunization.getHeader());
            if (value != null) {
                listHashMap.get(jsonImmunization.getHeader()).add(jsonImmunization);
            } else {
                // Key might be present...
                if (listHashMap.containsKey(jsonImmunization.getHeader())) {
                    listHashMap.put(jsonImmunization.getHeader(),new ArrayList<JsonImmunization>());
                    listHashMap.get(jsonImmunization.getHeader()).add(jsonImmunization);
                } else {
                    // Definitely no such key
                    listHashMap.put(jsonImmunization.getHeader(),new ArrayList<JsonImmunization>());
                    listHashMap.get(jsonImmunization.getHeader()).add(jsonImmunization);
                }
            }
        }
        for (Map.Entry<String, ArrayList<JsonImmunization>> entry : listHashMap.entrySet()) {
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
