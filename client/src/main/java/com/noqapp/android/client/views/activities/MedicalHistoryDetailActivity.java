package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.adapters.MedicalRecordAdapter;
import com.noqapp.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.common.beans.medical.JsonMedicalRecord;
import com.noqapp.common.model.types.medical.PhysicalExamEnum;

import java.util.ArrayList;
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
    private ListView listview;
    private MedicalRecordAdapter adapter;
    private List<JsonMedicalMedicine> medicalRecordList = new ArrayList<>();
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

        JsonMedicalPhysical jsonMedicalPhysicalExaminations = jsonMedicalRecord.getMedicalPhysical();
        listview = findViewById(R.id.listview);
        medicalRecordList = jsonMedicalRecord.getMedicalMedicines();
        adapter = new MedicalRecordAdapter(this, medicalRecordList);
        listview.setAdapter(adapter);
        for(PhysicalExamEnum physicalExam : PhysicalExamEnum.values()) {
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
            switch (physicalExam) {
                case BP:
                    mType.setText(physicalExam.getDescription() + ":" +" ");
                            //+ jsonMedicalPhysicalExaminations.getBloodPressure());
                    break;
                case PL:
                    mType.setText(physicalExam.getDescription() + ":" +" ");
                            //+ jsonMedicalPhysicalExaminations.getPluse());
                    break;
                case WT:
                    mType.setText(physicalExam.getDescription() + ":" +" ");
                            //+ jsonMedicalPhysicalExaminations.getWeight());
                    break;
            }
            childLayout.addView(mType, 0);
            ll_physical.addView(childLayout);
        }
    }

}
