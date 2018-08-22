package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.PhysicalGeneralExamEnum;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryDetailActivity extends BaseActivity {

    @BindView(R.id.ll_main)
    protected LinearLayout ll_main;
    @BindView(R.id.tv_complaints)
    protected TextView tv_complaints;
    @BindView(R.id.tv_past_history)
    protected TextView tv_past_history;
    @BindView(R.id.tv_family_history)
    protected TextView tv_family_history;
    @BindView(R.id.tv_patient_name)
    protected TextView tv_patient_name;
    @BindView(R.id.tv_diagnosed_by)
    protected TextView tv_diagnosed_by;
    @BindView(R.id.tv_business_name)
    protected TextView tv_business_name;
    @BindView(R.id.tv_known_allergy)
    protected TextView tv_known_allergy;
    @BindView(R.id.tv_clinical_finding)
    protected TextView tv_clinical_finding;
    @BindView(R.id.tv_provisional)
    protected TextView tv_provisional;

    @BindView(R.id.tv_instruction)
    protected TextView tv_instruction;

    @BindView(R.id.tv_followup)
    protected TextView tv_followup;

    @BindView(R.id.ll_physical)
    protected LinearLayout ll_physical;
    @BindView(R.id.ll_medication)
    protected LinearLayout ll_medication;

    @BindView(R.id.ll_investigation_pathology)
    protected LinearLayout ll_investigation_pathology;

    @BindView(R.id.ll_investigation_radiology)
    protected LinearLayout ll_investigation_radiology;


    @BindView(R.id.ll_complaints)
    protected LinearLayout ll_complaints;

    @BindView(R.id.ll_past_history)
    protected LinearLayout ll_past_history;

    @BindView(R.id.ll_family_history)
    protected LinearLayout ll_family_history;

    @BindView(R.id.ll_known_allergies)
    protected LinearLayout ll_known_allergies;

    @BindView(R.id.ll_clinical_finding)
    protected LinearLayout ll_clinical_finding;

    @BindView(R.id.ll_provisional)
    protected LinearLayout ll_provisional;

    @BindView(R.id.ll_instruction)
    protected LinearLayout ll_instruction;


    @BindView(R.id.ll_followup)
    protected LinearLayout ll_followup;
    @BindView(R.id.ll_radiology)
    protected LinearLayout ll_radiology;
    @BindView(R.id.ll_pathology)
    protected LinearLayout ll_pathology;


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
        tv_instruction.setText(jsonMedicalRecord.getPlanToPatient());
        tv_followup.setText(jsonMedicalRecord.getFollowUpInDays());

        tv_diagnosed_by.setText(jsonMedicalRecord.getDiagnosedById() + " (" + jsonMedicalRecord.getBizCategoryName() + ")");
        tv_business_name.setText(jsonMedicalRecord.getBusinessName());
        List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
        profileList.add(0, NoQueueBaseActivity.getUserProfile());
        tv_patient_name.setText(AppUtilities.getNameFromQueueUserID(jsonMedicalRecord.getQueueUserId(), profileList));

        if (tv_complaints.getText().toString().equals("")) {
            ll_complaints.setVisibility(View.GONE);
        }
        if (tv_past_history.getText().toString().equals("")) {
            ll_past_history.setVisibility(View.GONE);
        }
        if (tv_family_history.getText().toString().equals("")) {
            ll_family_history.setVisibility(View.GONE);
        }

        if (tv_known_allergy.getText().toString().equals("")) {
            ll_known_allergies.setVisibility(View.GONE);
        }

        if (tv_clinical_finding.getText().toString().equals("")) {
            ll_clinical_finding.setVisibility(View.GONE);
        }

        if (tv_provisional.getText().toString().equals("")) {
            ll_provisional.setVisibility(View.GONE);
        }

        if (tv_instruction.getText().toString().equals("")) {
            ll_instruction.setVisibility(View.GONE);
        }


        if (tv_followup.getText().toString().equals("")) {
            ll_followup.setVisibility(View.GONE);
        }


        JsonMedicalPhysical jsonMedicalPhysicalExaminations = jsonMedicalRecord.getMedicalPhysical();
        listview = findViewById(R.id.listview);
        medicalRecordList = jsonMedicalRecord.getMedicalMedicines();
        adapter = new MedicalRecordAdapter(this, medicalRecordList);
        listview.setAdapter(adapter);
        if (medicalRecordList.size() == 0)
            ll_medication.setVisibility(View.GONE);
        for (PhysicalGeneralExamEnum physicalExam : PhysicalGeneralExamEnum.values()) {
            LinearLayout childLayout = new LinearLayout(this);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            childLayout.setLayoutParams(linearParams);
            TextView mType = new TextView(this);
            mType.setTextSize(12);
            mType.setPadding(5, 3, 0, 3);
            mType.setTextColor(Color.BLACK);
            mType.setGravity(Gravity.LEFT | Gravity.CENTER);
            switch (physicalExam) {
                case TE:
                    mType.setText(physicalExam.getDescription() + ": "
                            + jsonMedicalPhysicalExaminations.getTemperature());
                    break;
                case BP:
                    mType.setText(physicalExam.getDescription() + ": "
                            + jsonMedicalPhysicalExaminations.getBloodPressure()[0]);
                    break;
                case PL:
                    mType.setText(physicalExam.getDescription() + ": "
                            + jsonMedicalPhysicalExaminations.getPluse());
                    break;
                case OX:
                    mType.setText(physicalExam.getDescription() + ": "
                            + jsonMedicalPhysicalExaminations.getOxygen());
                    break;
                case WT:
                    mType.setText(physicalExam.getDescription() + ": "
                            + jsonMedicalPhysicalExaminations.getWeight());
                    break;
            }
            childLayout.addView(mType, 0);
            ll_physical.addView(childLayout);
        }
        if (jsonMedicalRecord.getMedicalPathologies().size() == 0)
            ll_pathology.setVisibility(View.GONE);
        else {
            for (int i = 0; i < jsonMedicalRecord.getMedicalPathologies().size(); i++) {
                LinearLayout childLayout = new LinearLayout(this);
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayout.setLayoutParams(linearParams);
                TextView mType = new TextView(this);
                mType.setTextSize(12);
                mType.setTextColor(Color.BLACK);
                mType.setPadding(5, 3, 0, 3);
                mType.setGravity(Gravity.LEFT | Gravity.CENTER);
                mType.setText(jsonMedicalRecord.getMedicalPathologies().get(i).getName());
                childLayout.addView(mType, 0);
                ll_investigation_pathology.addView(childLayout);
            }
        }
        if (jsonMedicalRecord.getMedicalRadiologies().size() == 0)
            ll_radiology.setVisibility(View.GONE);
        else {
            for (int i = 0; i < jsonMedicalRecord.getMedicalRadiologies().size(); i++) {
                LinearLayout childLayout = new LinearLayout(this);
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayout.setLayoutParams(linearParams);
                TextView mType = new TextView(this);
                mType.setTextSize(12);
                mType.setTextColor(Color.BLACK);
                mType.setPadding(5, 3, 0, 3);
                mType.setGravity(Gravity.LEFT | Gravity.CENTER);
                mType.setText(jsonMedicalRecord.getMedicalRadiologies().get(i).getName());
                childLayout.addView(mType, 0);
                ll_investigation_radiology.addView(childLayout);
            }
        }
    }

}
