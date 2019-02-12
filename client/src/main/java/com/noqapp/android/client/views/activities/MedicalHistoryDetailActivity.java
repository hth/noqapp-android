package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiologyList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.medical.PhysicalGeneralExamEnum;

import org.apache.commons.lang3.StringUtils;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
        RecyclerView rv_thumb_images = findViewById(R.id.rv_thumb_images);
        rv_thumb_images.setHasFixedSize(true);
        rv_thumb_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.medical_history_details));
        JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getIntent().getExtras().getSerializable("data");
        if (null != jsonMedicalRecord.getImages() && jsonMedicalRecord.getImages().size() > 0) {
            ThumbnailGalleryAdapter thumbnailGalleryAdapter = new ThumbnailGalleryAdapter(this, jsonMedicalRecord.getImages(), true, jsonMedicalRecord.getRecordReferenceId());
            rv_thumb_images.setAdapter(thumbnailGalleryAdapter);
        }
        tv_complaints.setText(parseCheifComplanits(jsonMedicalRecord.getChiefComplain()));
        tv_past_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory());
        tv_family_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory());
        tv_known_allergy.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies());
        tv_clinical_finding.setText(jsonMedicalRecord.getClinicalFinding());
        tv_provisional.setText(jsonMedicalRecord.getProvisionalDifferentialDiagnosis());
        tv_instruction.setText(jsonMedicalRecord.getPlanToPatient());
        tv_followup.setText(jsonMedicalRecord.getFollowUpInDays());

        if (jsonMedicalRecord.getBusinessType() == BusinessTypeEnum.DO) {
            tv_diagnosed_by.setText("Dr. " + jsonMedicalRecord.getDiagnosedByDisplayName());
        } else {
            tv_diagnosed_by.setText(jsonMedicalRecord.getDiagnosedByDisplayName());
        }
        tv_business_name.setText(jsonMedicalRecord.getBusinessName());
        tv_business_category_name.setText(jsonMedicalRecord.getBizCategoryName());
        tv_create.setText(jsonMedicalRecord.getCreateDate());
        tv_no_of_time_access.setText("# of times record viewed: " + jsonMedicalRecord.getRecordAccess().size());

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
        if (medicalRecordList.isEmpty()) {
            ll_medication.setVisibility(View.GONE);
        }
        String notAvailable = "N/A";
        if (null != jsonMedicalPhysicalExaminations)
            for (PhysicalGeneralExamEnum physicalExam : PhysicalGeneralExamEnum.values()) {
                String label = "";
                switch (physicalExam) {
                    case TE:
                        if (null != jsonMedicalPhysicalExaminations.getTemperature()) {
                            label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getTemperature();
                        } else {
                            label = physicalExam.getDescription() + ": " + notAvailable;
                        }
                        break;
                    case BP:
                        if (null != jsonMedicalPhysicalExaminations.getBloodPressure() && jsonMedicalPhysicalExaminations.getBloodPressure().length > 0) {
                            label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getBloodPressure()[0] + "/" + jsonMedicalPhysicalExaminations.getBloodPressure()[1];
                        } else {
                            label = physicalExam.getDescription() + ": " + notAvailable;
                        }
                        break;
                    case PL:
                        if (null != jsonMedicalPhysicalExaminations.getPulse()) {
                            label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getPulse();
                        } else {
                            label = physicalExam.getDescription() + ": " + notAvailable;
                        }
                        break;
                    case OX:
                        if (null != jsonMedicalPhysicalExaminations.getOxygen()) {
                            label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getOxygen();
                        } else {
                            label = physicalExam.getDescription() + ": " + notAvailable;
                        }
                        break;
                    case WT:
                        if (null != jsonMedicalPhysicalExaminations.getWeight()) {
                            label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getWeight();
                        } else {
                            label = physicalExam.getDescription() + ": " + notAvailable;
                        }
                        break;
                    case HT:
                        if (null != jsonMedicalPhysicalExaminations.getHeight()) {
                            label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getHeight();
                        } else {
                            label = physicalExam.getDescription() + ": " + notAvailable;
                        }
                        break;
                    case RP:
                        if (null != jsonMedicalPhysicalExaminations.getRespiratory()) {
                            label = physicalExam.getDescription() + ": " + jsonMedicalPhysicalExaminations.getRespiratory();
                        } else {
                            label = physicalExam.getDescription() + ": " + notAvailable;
                        }
                        break;
                }
                ll_physical.addView(getView(label));
            }
        if (jsonMedicalRecord.getMedicalPathologies().isEmpty()) {
            ll_pathology.setVisibility(View.GONE);
        } else {
            for (JsonMedicalPathology jsonMedicalPathology : jsonMedicalRecord.getMedicalPathologies()) {
                ll_investigation_pathology.addView(getView(jsonMedicalPathology.getName()));
            }
        }
        if (jsonMedicalRecord.getMedicalRadiologyLists().isEmpty()) {
            ll_radiology.setVisibility(View.GONE);
        } else {
            for (JsonMedicalRadiologyList jsonMedicalRadiologyList : jsonMedicalRecord.getMedicalRadiologyLists()) {
                List<JsonMedicalRadiology> jsonMedicalRadiologies = jsonMedicalRadiologyList.getJsonMedicalRadiologies();
                for (JsonMedicalRadiology jsonMedicalRadiology : jsonMedicalRadiologies) {
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

    public String parseCheifComplanits(String str) {
        if(TextUtils.isEmpty(str))
            return "";
        String cheifComplanits = "";
        try {
            String[] temp = str.split("\\r?\\n");
            if (null != temp && temp.length > 0) {
                for (int i = 0; i < temp.length; i++) {
                    String act = temp[i];
                    if (act.contains("|")) {
                        String[] strArray = act.split("\\|");
                        String shortName = strArray[0];
                        String val = strArray[1];
                        if (i < temp.length - 1) {
                            cheifComplanits += "Having " + shortName + " since last " + val + "." + "\n";
                        } else {
                            cheifComplanits += "Having " + shortName + " since last " + val + ".";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cheifComplanits;
    }
}
