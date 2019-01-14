package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.PhysicalGeneralExamEnum;

import org.apache.commons.lang3.StringUtils;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MedicalHistoryDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);

        TextView tv_diagnosed_by = findViewById(R.id.tv_diagnosed_by);
        TextView tv_business_name = findViewById(R.id.tv_business_name);
        TextView tv_business_category_name = findViewById(R.id.tv_business_category_name);
        TextView tv_complaints = findViewById(R.id.tv_complaints_temp);
        TextView tv_create = findViewById(R.id.tv_create);
        TextView tv_no_of_time_access = findViewById(R.id.tv_no_of_time_access);
        TextView tv_past_history = findViewById(R.id.tv_past_history);
        TextView tv_family_history = findViewById(R.id.tv_family_history);
        TextView tv_patient_name = findViewById(R.id.tv_patient_name);

        TextView tv_known_allergy = findViewById(R.id.tv_known_allergy);
        TextView tv_clinical_finding = findViewById(R.id.tv_clinical_finding);
        TextView tv_provisional = findViewById(R.id.tv_provisional);
        TextView tv_instruction = findViewById(R.id.tv_instruction);
        TextView tv_followup = findViewById(R.id.tv_followup);
        LinearLayout ll_physical = findViewById(R.id.ll_physical);
        LinearLayout ll_medication = findViewById(R.id.ll_medication);
        LinearLayout ll_investigation_pathology = findViewById(R.id.ll_investigation_pathology);
        LinearLayout ll_investigation_radiology = findViewById(R.id.ll_investigation_radiology);
        LinearLayout ll_complaints = findViewById(R.id.ll_complaints);
        LinearLayout ll_past_history = findViewById(R.id.ll_past_history);
        LinearLayout ll_family_history = findViewById(R.id.ll_family_history);
        LinearLayout ll_known_allergies = findViewById(R.id.ll_known_allergies);
        LinearLayout ll_clinical_finding = findViewById(R.id.ll_clinical_finding);
        LinearLayout ll_provisional = findViewById(R.id.ll_provisional);
        LinearLayout ll_instruction = findViewById(R.id.ll_instruction);
        LinearLayout ll_followup = findViewById(R.id.ll_followup);
        LinearLayout ll_radiology = findViewById(R.id.ll_radiology);
        LinearLayout ll_pathology = findViewById(R.id.ll_pathology);
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.medical_history_details));
        JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getIntent().getExtras().getSerializable("data");
        tv_complaints.setText(jsonMedicalRecord.getChiefComplain());
        tv_past_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory());
        tv_family_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory());
        tv_known_allergy.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies());
        tv_clinical_finding.setText(jsonMedicalRecord.getClinicalFinding());
        tv_provisional.setText(jsonMedicalRecord.getProvisionalDifferentialDiagnosis());
        tv_instruction.setText(jsonMedicalRecord.getPlanToPatient());
        tv_followup.setText(jsonMedicalRecord.getFollowUpInDays());

        tv_diagnosed_by.setText(jsonMedicalRecord.getDiagnosedByDisplayName());
        tv_business_name.setText(jsonMedicalRecord.getBusinessName());
        tv_business_category_name.setText(jsonMedicalRecord.getBizCategoryName());
        tv_complaints.setText(jsonMedicalRecord.getChiefComplain());
        tv_create.setText(jsonMedicalRecord.getCreateDate());
        tv_no_of_time_access.setText("No of times record view: " + jsonMedicalRecord.getRecordAccess().size());

        List<JsonProfile> profileList = NoQueueBaseActivity.getUserProfile().getDependents();
        profileList.add(0, NoQueueBaseActivity.getUserProfile());
        tv_patient_name.setText(AppUtilities.getNameFromQueueUserID(jsonMedicalRecord.getQueueUserId(), profileList));

        if (StringUtils.isBlank(tv_complaints.getText())) {
            ll_complaints.setVisibility(View.GONE);
        }
        if (StringUtils.isBlank(tv_past_history.getText())) {
            ll_past_history.setVisibility(View.GONE);
        }
        if (StringUtils.isBlank(tv_family_history.getText())) {
            ll_family_history.setVisibility(View.GONE);
        }
        if (StringUtils.isBlank(tv_known_allergy.getText())) {
            ll_known_allergies.setVisibility(View.GONE);
        }
        if (StringUtils.isBlank(tv_clinical_finding.getText())) {
            ll_clinical_finding.setVisibility(View.GONE);
        }
        if (StringUtils.isBlank(tv_provisional.getText())) {
            ll_provisional.setVisibility(View.GONE);
        }
        if (StringUtils.isBlank(tv_instruction.getText())) {
            ll_instruction.setVisibility(View.GONE);
        }
        if (StringUtils.isBlank(tv_followup.getText())) {
            ll_followup.setVisibility(View.GONE);
        }
        JsonMedicalPhysical jsonMedicalPhysicalExaminations = jsonMedicalRecord.getMedicalPhysical();
        ListView listview = findViewById(R.id.listview);
        List<JsonMedicalMedicine> medicalRecordList = jsonMedicalRecord.getMedicalMedicines();
        MedicalRecordAdapter adapter = new MedicalRecordAdapter(this, medicalRecordList);
        listview.setAdapter(adapter);
        if (0 == medicalRecordList.size()) {
            ll_medication.setVisibility(View.GONE);
        }
        if (null != jsonMedicalPhysicalExaminations)
            for (PhysicalGeneralExamEnum physicalExam : PhysicalGeneralExamEnum.values()) {
                String label = "";
                switch (physicalExam) {
                    case TE:
                        label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getTemperature();
                        break;
                    case BP:
                        label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getBloodPressure()[0];
                        break;
                    case PL:
                        label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getPulse();
                        break;
                    case OX:
                        label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getOxygen();
                        break;
                    case WT:
                        label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getWeight();
                        break;
                    case HT:
                        label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getHeight();
                        break;
                    case RP:
                        label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getRespiratory();
                        break;
                }
                ll_physical.addView(getView(label));
            }
        if (jsonMedicalRecord.getMedicalPathologies().size() == 0) {
            ll_pathology.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < jsonMedicalRecord.getMedicalPathologies().size(); i++) {
                ll_investigation_pathology.addView(getView(jsonMedicalRecord.getMedicalPathologies().get(i).getName()));
            }
        }
        if (jsonMedicalRecord.getMedicalRadiologyLists().size() == 0) {
            ll_radiology.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < jsonMedicalRecord.getMedicalRadiologyLists().size(); i++) {
                List<JsonMedicalRadiology> radioList = jsonMedicalRecord.getMedicalRadiologyLists().get(i).getJsonMedicalRadiologies();
                for (JsonMedicalRadiology jsonMedicalRadiology : radioList) {
                    ll_investigation_radiology.addView(getView(jsonMedicalRadiology.getName()));
                }
            }
        }
    }

    private LinearLayout getView(String label) {
        LinearLayout childLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        childLayout.setLayoutParams(linearParams);
        TextView mType = new TextView(this);
        mType.setTextSize(16);
        mType.setTextColor(Color.BLACK);
        mType.setPadding(20, 3, 0, 3);
        mType.setGravity(Gravity.LEFT | Gravity.CENTER);
        mType.setText(label);
        childLayout.addView(mType, 0);
        return childLayout;
    }
}
