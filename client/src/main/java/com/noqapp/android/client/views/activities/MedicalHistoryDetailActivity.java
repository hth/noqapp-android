package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.medical.JsonMedicalPhysicalExamination;
import com.noqapp.android.client.presenter.beans.medical.JsonMedicalRecord;
import com.noqapp.android.client.presenter.beans.medical.JsonMedicine;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicalHistoryDetailActivity extends BaseActivity {

    @BindView(R.id.ll_main)
    protected LinearLayout ll_main;

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
    @BindView(R.id.tv_provisional)
    protected TextView tv_provisional;
    @BindView(R.id.tv_investigation)
    protected TextView tv_investigation;
    @BindView(R.id.ll_physical)
    protected LinearLayout ll_physical;
    @BindView(R.id.ll_medication)
    protected LinearLayout ll_medication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);
        ButterKnife.bind(this);
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.medical_history_details));
        JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getIntent().getExtras().getSerializable("data");
        tv_complaints.setText(jsonMedicalRecord.getChiefComplain());
        tv_past_history.setText(jsonMedicalRecord.getPastHistory());
        tv_family_history.setText(jsonMedicalRecord.getFamilyHistory());
        tv_known_allergy.setText(jsonMedicalRecord.getKnownAllergies());
        tv_clinical_finding.setText(jsonMedicalRecord.getClinicalFinding());
        tv_provisional.setText(jsonMedicalRecord.getProvisionalDifferentialDiagnosis());
        //  tv_investigation.setText(jsonMedicalRecord.get);

        List<JsonMedicalPhysicalExamination> data = jsonMedicalRecord.getMedicalPhysicalExaminations();
        for (int j = 0; j < data.size(); j++) {

            LinearLayout childLayout = new LinearLayout(this);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            childLayout.setLayoutParams(linearParams);
            TextView mType = new TextView(this);
            mType.setTextSize(17);
            mType.setPadding(5, 3, 0, 3);
            mType.setTypeface(Typeface.DEFAULT_BOLD);
            mType.setGravity(Gravity.LEFT | Gravity.CENTER);
            mType.setText(data.get(j).getValue() + ":" + data.get(j).getTestResult());
            childLayout.addView(mType, 0);
            ll_physical.addView(childLayout);
        }
        List<JsonMedicine> medicinedata = jsonMedicalRecord.getMedicines();
        for (int j = 0; j < medicinedata.size(); j++) {

            LinearLayout childLayout = new LinearLayout(this);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            childLayout.setLayoutParams(linearParams);
            TextView mType = new TextView(this);
            mType.setTextSize(17);
            mType.setPadding(5, 3, 0, 3);
            mType.setTypeface(Typeface.DEFAULT_BOLD);
            mType.setGravity(Gravity.LEFT | Gravity.CENTER);
            mType.setText(medicinedata.get(j).getName() + ":" + medicinedata.get(j).getStrength() + ":" + medicinedata.get(j).getTimes());
            childLayout.addView(mType, 0);
            ll_medication.addView(childLayout);
        }
    }

}
