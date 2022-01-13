package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiologyList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;
import com.noqapp.android.common.utils.CommonHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);

        ImageView iv_profile = findViewById(R.id.iv_profile);
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
        TextView tv_weight = findViewById(R.id.tv_weight);
        TextView tv_pulse = findViewById(R.id.tv_pulse);
        TextView tv_temperature = findViewById(R.id.tv_temperature);
        TextView tv_height = findViewById(R.id.tv_height);
        TextView tv_bp = findViewById(R.id.tv_bp);
        TextView tv_respiration = findViewById(R.id.tv_respiration);
        RecyclerView rv_thumb_images = findViewById(R.id.rv_thumb_images);
        rv_thumb_images.setHasFixedSize(true);
        rv_thumb_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.medical_history_details));
        JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA);
        if (null != jsonMedicalRecord.getImages() && jsonMedicalRecord.getImages().size() > 0) {
            ThumbnailGalleryAdapter thumbnailGalleryAdapter = new ThumbnailGalleryAdapter(this, jsonMedicalRecord.getImages(), true, jsonMedicalRecord.getRecordReferenceId());
            rv_thumb_images.setAdapter(thumbnailGalleryAdapter);
        }
        if (BusinessTypeEnum.DO == jsonMedicalRecord.getBusinessType()) {
            iv_profile.setImageResource(R.drawable.doctor);
        } else {
            iv_profile.setImageResource(R.drawable.lab);
        }
        tv_complaints.setText(parseCheifComplanits(jsonMedicalRecord.getChiefComplain()));
        tv_past_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getPastHistory());
        tv_family_history.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getFamilyHistory());
        tv_known_allergy.setText(jsonMedicalRecord.getJsonUserMedicalProfile().getKnownAllergies());
        tv_clinical_finding.setText(jsonMedicalRecord.getClinicalFinding());
        tv_provisional.setText(jsonMedicalRecord.getProvisionalDifferentialDiagnosis());
        tv_instruction.setText(jsonMedicalRecord.getPlanToPatient());
        tv_followup.setText(jsonMedicalRecord.getFollowUpInDays() + " Days");

        if (jsonMedicalRecord.getBusinessType() == BusinessTypeEnum.DO) {
            tv_diagnosed_by.setText("Dr. " + jsonMedicalRecord.getDiagnosedByDisplayName());
        } else {
            tv_diagnosed_by.setText(jsonMedicalRecord.getDiagnosedByDisplayName());
        }
        tv_diagnosed_by.setVisibility(TextUtils.isEmpty(jsonMedicalRecord.getDiagnosedByDisplayName()) ? View.GONE : View.VISIBLE);
        tv_business_name.setText(jsonMedicalRecord.getBusinessName());
        tv_business_category_name.setText(jsonMedicalRecord.getBizCategoryName());
        try {
            tv_create.setText(CommonHelper.SDF_YYYY_MM_DD_HH_MM_A.format(CommonHelper.SDF_ISO8601_FMT.parse(jsonMedicalRecord.getCreateDate())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_no_of_time_access.setText("# of times record viewed: " + jsonMedicalRecord.getRecordAccess().size());

        List<JsonProfile> profileList = NoQueueClientApplication.getAllProfileList();
        tv_patient_name.setText(AppUtils.getNameFromQueueUserID(jsonMedicalRecord.getQueueUserId(), profileList));

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
        if (TextUtils.isEmpty(jsonMedicalRecord.getFollowUpInDays())) {
            ll_followup.setVisibility(View.GONE);
        }

        ListView listview = findViewById(R.id.listview);
        List<JsonMedicalMedicine> medicalRecordList = jsonMedicalRecord.getMedicalMedicines();
        MedicalRecordAdapter adapter = new MedicalRecordAdapter(this, medicalRecordList);
        listview.setAdapter(adapter);
        if (medicalRecordList.isEmpty()) {
            ll_medication.setVisibility(View.GONE);
        }
        String notAvailable = "N/A";
        LinearLayout ll_physical = findViewById(R.id.ll_physical);
        if (jsonMedicalRecord.getBusinessType() == BusinessTypeEnum.HS) {
            ll_physical.setVisibility(View.GONE);
        } else {
            ll_physical.setVisibility(View.VISIBLE);

            if (null != jsonMedicalRecord.getMedicalPhysical()) {
                if (null != jsonMedicalRecord.getMedicalPhysical().getRespiratory()) {
                    tv_respiration.setText(jsonMedicalRecord.getMedicalPhysical().getRespiratory());
                } else {
                    tv_respiration.setText(notAvailable);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getHeight()) {
                    tv_height.setText(jsonMedicalRecord.getMedicalPhysical().getHeight());
                } else {
                    tv_height.setText(notAvailable);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getPulse()) {
                    tv_pulse.setText(jsonMedicalRecord.getMedicalPhysical().getPulse());
                } else {
                    tv_pulse.setText(notAvailable);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                    tv_bp.setText(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0] + "/" + jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1]);
                } else {
                    tv_bp.setText(notAvailable);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
                    tv_weight.setText(jsonMedicalRecord.getMedicalPhysical().getWeight());
                } else {
                    tv_weight.setText(notAvailable);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
                    tv_temperature.setText(jsonMedicalRecord.getMedicalPhysical().getTemperature());
                } else {
                    tv_temperature.setText(notAvailable);
                }
            } else {
                tv_pulse.setText(notAvailable);
                tv_bp.setText(notAvailable);
                tv_weight.setText(notAvailable);
                tv_temperature.setText(notAvailable);
                tv_respiration.setText(notAvailable);
                tv_height.setText(notAvailable);
            }
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
        ll_scan.setVisibility(View.GONE);
        ll_spec.setVisibility(View.GONE);
        ll_xray.setVisibility(View.GONE);
        ll_path.setVisibility(View.GONE);
        ll_mri.setVisibility(View.GONE);
        ll_sono.setVisibility(View.GONE);

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
            tv_attachment_pathology.setText("" + jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages().size());
            if (TextUtils.isEmpty(jsonMedicalRecord.getMedicalPathologiesLists().get(0).getObservation())) {
                tv_observation_pathology_label.setText("N/A");
            } else {
                tv_observation_pathology_label.setText(jsonMedicalRecord.getMedicalPathologiesLists().get(0).getObservation());
            }
            tv_attachment_pathology.setOnClickListener((View v) -> {
                callSliderScreen(jsonMedicalRecord.getMedicalPathologiesLists().get(0).getImages(), jsonMedicalRecord.getMedicalPathologiesLists().get(0).getRecordReferenceId());
            });
            ll_path.setVisibility(View.VISIBLE);
        } else {
            tv_attachment_pathology.setText("No Attachment");
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
                switch (labCategory) {
                    case SPEC:
                        tv_attachment_spec.setText(value);
                        tv_attachment_spec.setOnClickListener((View v) -> {
                            callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                        });
                        tv_observation_spec_label.setText(observation);
                        ll_spec.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case SONO:
                        tv_attachment_sono.setText(value);
                        tv_attachment_sono.setOnClickListener((View v) -> {
                            callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                        });
                        tv_observation_sono_label.setText(observation);
                        ll_sono.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case XRAY:
                        tv_attachment_xray.setText(value);
                        tv_attachment_xray.setOnClickListener((View v) -> {
                            callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                        });
                        tv_observation_xray_label.setText(observation);
                        ll_xray.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
//                    case PATH:
//                        tv_attachment_pathology.setText(value);
//                        tv_attachment_pathology.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
//                            }
//                        });
//                        tv_observation_pathology_label.setText(observation);
//                        ll_path.setVisibility(showLayout ? View.VISIBLE : View.GONE);
//                        break;
                    case MRI:
                        tv_attachment_mri.setText(value);
                        tv_attachment_mri.setOnClickListener((View v) -> {
                            callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                        });
                        tv_observation_mri_label.setText(observation);
                        ll_mri.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    case SCAN:
                        tv_attachment_scan.setText(value);
                        tv_attachment_scan.setOnClickListener((View v) -> {
                            callSliderScreen(jsonMedicalRadiologyList.getImages(), jsonMedicalRadiologyList.getRecordReferenceId());
                        });
                        tv_observation_scan_label.setText(observation);
                        ll_scan.setVisibility(showLayout ? View.VISIBLE : View.GONE);
                        break;
                    default:
                }
            }
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
