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
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;
import com.noqapp.android.common.model.types.medical.PhysicalGeneralExamEnum;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
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
        if (null == jsonMedicalRecord.getMedicalPathologiesLists() || jsonMedicalRecord.getMedicalPathologiesLists().size() == 0
                || jsonMedicalRecord.getMedicalPathologiesLists().get(0).getJsonMedicalPathologies().isEmpty()) {
            ll_pathology.setVisibility(View.GONE);
        } else {
            for (JsonMedicalPathology jsonMedicalPathology : jsonMedicalRecord.getMedicalPathologiesLists().get(0).getJsonMedicalPathologies()) {
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

        LinearLayout ll_xray = findViewById(R.id.ll_xray);
        LinearLayout ll_sono = findViewById(R.id.ll_sono);
        LinearLayout ll_scan = findViewById(R.id.ll_scan);
        LinearLayout ll_path = findViewById(R.id.ll_path);
        LinearLayout ll_spec = findViewById(R.id.ll_spec);
        LinearLayout ll_mri = findViewById(R.id.ll_mri);

        TextView tv_attachment_xray = findViewById(R.id.tv_attachment_xray);
        TextView tv_attachment_sono = findViewById(R.id.tv_attachment_sono);
        TextView tv_attachment_scan = findViewById(R.id.tv_attachment_scan);
        TextView tv_attachment_pathology = findViewById(R.id.tv_attachment_pathology);
        TextView tv_attachment_spec = findViewById(R.id.tv_attachment_spec);
        TextView tv_attachment_mri = findViewById(R.id.tv_attachment_mri);

        TextView tv_observation_xray_label = findViewById(R.id.tv_observation_xray_label);
        TextView tv_observation_sono_label = findViewById(R.id.tv_observation_sono_label);
        TextView tv_observation_scan_label = findViewById(R.id.tv_observation_scan_label);
        TextView tv_observation_pathology_label = findViewById(R.id.tv_observation_pathology_label);
        TextView tv_observation_spec_label = findViewById(R.id.tv_observation_spec_label);
        TextView tv_observation_mri_label = findViewById(R.id.tv_observation_mri_label);

        if (null != jsonMedicalRecord.getMedicalPathologiesLists() && jsonMedicalRecord.getMedicalPathologiesLists().size() > 0
                && jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages() != null &&
                jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages().size() > 0) {
            tv_attachment_pathology.setText("Attachment Available: " + jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages().size());
            tv_observation_pathology_label.setText(jsonMedicalRecord.getMedicalPathologiesLists().get(0).getObservation());
            ll_path.setVisibility(View.VISIBLE);
        } else {
            tv_attachment_pathology.setText("No Attachment Available");
            tv_observation_pathology_label.setText("N/A");
            ll_path.setVisibility(View.GONE);
        }
        if (null != jsonMedicalRecord.getMedicalRadiologyLists() && jsonMedicalRecord.getMedicalRadiologyLists().size() > 0) {
            for (int i = 0; i < jsonMedicalRecord.getMedicalRadiologyLists().size(); i++) {
                final JsonMedicalRadiologyList jsonMedicalRadiologyList = jsonMedicalRecord.getMedicalRadiologyLists().get(i);
                LabCategoryEnum labCategory = jsonMedicalRadiologyList.getLabCategory();
                String value, observation;
                boolean showLayout;
                if (null != jsonMedicalRadiologyList.getImages() && jsonMedicalRadiologyList.getImages().size() > 0) {
                    value = "" + jsonMedicalRadiologyList.getImages().size();
                    showLayout = true;
                } else {
                    value = "No Attachment";
                    showLayout = false;
                }
                if (TextUtils.isEmpty(jsonMedicalRadiologyList.getObservation())) {
                    observation = "N/A";
                } else {
                    observation = jsonMedicalRadiologyList.getObservation();
                }
                ll_scan.setVisibility(View.GONE);
                ll_spec.setVisibility(View.GONE);
                ll_xray.setVisibility(View.GONE);
                ll_path.setVisibility(View.GONE);
                ll_mri.setVisibility(View.GONE);
                ll_sono.setVisibility(View.GONE);
                switch (labCategory) {
                    case SPEC:
                        tv_attachment_spec.setText(value);
                        tv_attachment_spec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                            }
                        });
                        tv_observation_spec_label.setText(observation);
                        ll_spec.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case SONO:
                        tv_attachment_sono.setText(value);
                        tv_attachment_sono.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                            }
                        });
                        tv_observation_sono_label.setText(observation);
                        ll_sono.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case XRAY:
                        tv_attachment_xray.setText(value);
                        tv_attachment_xray.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                            }
                        });
                        tv_observation_xray_label.setText(observation);
                        ll_xray.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case PATH:
                        tv_attachment_pathology.setText(value);
                        tv_attachment_pathology.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                            }
                        });
                        tv_observation_pathology_label.setText(observation);
                        ll_path.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case MRI:
                        tv_attachment_mri.setText(value);
                        tv_attachment_mri.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                            }
                        });
                        tv_observation_mri_label.setText(observation);
                        ll_mri.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case SCAN:
                        tv_attachment_scan.setText(value);
                        tv_attachment_scan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                            }
                        });
                        tv_observation_scan_label.setText(observation);
                        ll_scan.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    default:
                }
            }
        } else {
            ll_scan.setVisibility(View.GONE);
            ll_spec.setVisibility(View.GONE);
            ll_xray.setVisibility(View.GONE);
            ll_path.setVisibility(View.GONE);
            ll_mri.setVisibility(View.GONE);
            ll_sono.setVisibility(View.GONE);
        }
    }

    private void callSliderScreen(List<String> images, String recordReferenceId) {
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("imageurls", (ArrayList<String>) images);
        intent.putExtra("isDocument", true);
        intent.putExtra("recordReferenceId", recordReferenceId);
        startActivity(intent);
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
        if (TextUtils.isEmpty(str))
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
