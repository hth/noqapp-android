package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonMedicalRecord;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicalHistoryDetailActivity extends AppCompatActivity {

    @BindView(R.id.ll_main)
    protected LinearLayout ll_main;
    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;

    @BindView(R.id.tv_complaints)
    protected TextView tv_complaints;
    @BindView(R.id.tv_past_history)
    protected TextView tv_past_history;
    @BindView(R.id.tv_family_history)
    protected TextView tv_family_history;
    @BindView(R.id.tv_known_allergy)
    protected TextView tv_known_allergy;
    @BindView(R.id.tv_clinical_finding)
    protected TextView tv_clinical_finding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.medical_history_details));
        JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getIntent().getExtras().getSerializable("data");
        tv_complaints.setText(jsonMedicalRecord.getChiefComplain());
        tv_past_history.setText(jsonMedicalRecord.getPastHistory());
        tv_family_history.setText(jsonMedicalRecord.getFamilyHistory());
        tv_known_allergy.setText(jsonMedicalRecord.getKnownAllergies());
        tv_clinical_finding.setText(jsonMedicalRecord.getClinicalFinding());
    }

}
