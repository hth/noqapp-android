package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.client.R;
import com.noqapp.android.client.model.MedicalRecordApiModel;
import com.noqapp.android.client.presenter.MedicalRecordPresenter;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.MedicalHistoryAdapter;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicalHistoryActivity extends BaseActivity implements MedicalRecordPresenter {

    @BindView(R.id.listview)
    protected ListView listview;
    @BindView(R.id.tv_empty)
    protected TextView tv_empty;

    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);
        ButterKnife.bind(this);
        initActionsViews(false);

        tv_toolbar_title.setText(getString(R.string.medical_history));

        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        if(UserUtils.isLogin()) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                new MedicalRecordApiModel(this).getMedicalRecord(UserUtils.getEmail(), UserUtils.getAuth());
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }else {
            Toast.makeText(this,"Please login to see the details",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void medicalRecordResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (null != jsonMedicalRecordList & jsonMedicalRecordList.getJsonMedicalRecords().size() > 0) {
            jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
        }
        Collections.reverse(jsonMedicalRecords);
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(this, jsonMedicalRecords);
        listview.setAdapter(adapter);
        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(MedicalHistoryActivity.this, MedicalHistoryDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", jsonMedicalRecords.get(i));
                in.putExtras(bundle);
                startActivity(in);
            }
        });
        dismissProgress();
    }

    @Override
    public void medicalRecordError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
    }
}
