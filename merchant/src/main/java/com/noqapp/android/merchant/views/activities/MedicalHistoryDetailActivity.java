package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.merchant.R;


public class MedicalHistoryDetailActivity extends AppCompatActivity {

    protected LinearLayout ll_main;
    protected TextView tv_complaints;
    protected TextView tv_past_history;
    protected TextView tv_family_history;
    protected TextView tv_known_allergy;
    protected TextView tv_clinical_finding;
    protected TextView tv_provisional;

    protected TextView tv_investigation;
    protected LinearLayout ll_physical;
    protected LinearLayout ll_medication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);


      //  tv_toolbar_title.setText(getString(R.string.medical_history_details));
       // JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getIntent().getExtras().getSerializable("data");
//        tv_complaints.setText(jsonMedicalRecord.getChiefComplain());
//        tv_past_history.setText(jsonMedicalRecord.getPastHistory());
//        tv_family_history.setText(jsonMedicalRecord.getFamilyHistory());
//        tv_known_allergy.setText(jsonMedicalRecord.getKnownAllergies());
//        tv_clinical_finding.setText(jsonMedicalRecord.getClinicalFinding());
//        tv_provisional.setText(jsonMedicalRecord.getProvisionalDifferentialDiagnosis());
        //  tv_investigation.setText(jsonMedicalRecord.get);

//        List<JsonMedicalPhysicalExamination> data = jsonMedicalRecord.getMedicalPhysicalExaminations();
////        for (int j = 0; j < data.size(); j++) {
////
////            LinearLayout childLayout = new LinearLayout(this);
////            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
////                    LinearLayout.LayoutParams.WRAP_CONTENT,
////                    LinearLayout.LayoutParams.WRAP_CONTENT);
////            childLayout.setLayoutParams(linearParams);
////            TextView mType = new TextView(this);
////            mType.setTextSize(17);
////            mType.setPadding(5, 3, 0, 3);
////            mType.setTypeface(Typeface.DEFAULT_BOLD,0);
////            mType.setGravity(Gravity.LEFT | Gravity.CENTER);
////            mType.setText(data.get(j).getValue() + ":" + data.get(j).getTestResult());
////            childLayout.addView(mType, 0);
////            ll_physical.addView(childLayout);
////        }
////        List<JsonMedicine> medicinedata = jsonMedicalRecord.getMedicines();
////        for (int j = 0; j < medicinedata.size(); j++) {
////
////            LinearLayout childLayout = new LinearLayout(this);
////            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
////                    LinearLayout.LayoutParams.WRAP_CONTENT,
////                    LinearLayout.LayoutParams.WRAP_CONTENT);
////            childLayout.setLayoutParams(linearParams);
////            TextView mType = new TextView(this);
////            mType.setTextSize(17);
////            mType.setPadding(5, 3, 0, 3);
////            mType.setTypeface(Typeface.DEFAULT_BOLD,0);
////            mType.setGravity(Gravity.LEFT | Gravity.CENTER);
////            mType.setText(medicinedata.get(j).getName() + ":" + medicinedata.get(j).getStrength() + ":" + medicinedata.get(j).getTimes());
////            childLayout.addView(mType, 0);
////            ll_medication.addView(childLayout);
       // }
    }

}
