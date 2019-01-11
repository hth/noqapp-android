package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiologyList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.utils.PdfGenerator;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.merchant.views.pojos.MedicalCasePojo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class PrintFragment extends Fragment implements MedicalRecordPresenter {

    private TextView tv_patient_name, tv_address, tv_symptoms, tv_diagnosis, tv_instruction, tv_pathology, tv_clinical_findings, tv_examination, tv_provisional_diagnosis;
    private TextView tv_radio_xray, tv_radio_sono, tv_radio_scan, tv_radio_mri, tv_details, tv_followup;
    private TextView tv_weight, tv_height, tv_respiratory, tv_temperature, tv_bp, tv_pulse;
    private MedicalHistoryModel medicalHistoryModel;
    private SegmentedControl sc_follow_up;
    private Button btn_submit,btn_print_pdf;
    private ListView lv_medicine;
    private MedicalRecordAdapter adapter;
    private JsonPreferredBusinessList jsonPreferredBusinessList;
    private ProgressDialog progressDialog;
    private ArrayList<String> follow_up_data = new ArrayList<>();
    private String followup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_print, container, false);
        initProgress();
        medicalHistoryModel = new MedicalHistoryModel(this);
        tv_patient_name = v.findViewById(R.id.tv_patient_name);
        tv_address = v.findViewById(R.id.tv_address);
        tv_symptoms = v.findViewById(R.id.tv_symptoms);
        tv_details = v.findViewById(R.id.tv_details);
        tv_diagnosis = v.findViewById(R.id.tv_diagnosis);
        tv_instruction = v.findViewById(R.id.tv_instruction);
        tv_radio_mri = v.findViewById(R.id.tv_radio_mri);
        tv_radio_scan = v.findViewById(R.id.tv_radio_scan);
        tv_radio_sono = v.findViewById(R.id.tv_radio_sono);
        tv_radio_xray = v.findViewById(R.id.tv_radio_xray);
        tv_pathology = v.findViewById(R.id.tv_pathology);
        tv_clinical_findings = v.findViewById(R.id.tv_clinical_findings);
        tv_examination = v.findViewById(R.id.tv_examination);

        tv_weight = v.findViewById(R.id.tv_weight);
        tv_height = v.findViewById(R.id.tv_height);
        tv_respiratory = v.findViewById(R.id.tv_respiratory);
        tv_temperature = v.findViewById(R.id.tv_temperature);
        tv_bp = v.findViewById(R.id.tv_bp);
        tv_pulse = v.findViewById(R.id.tv_pulse);
        tv_followup = v.findViewById(R.id.tv_followup);
        tv_provisional_diagnosis = v.findViewById(R.id.tv_provisional_diagnosis);
        lv_medicine = v.findViewById(R.id.lv_medicine);
        sc_follow_up = v.findViewById(R.id.sc_follow_up);
        follow_up_data.clear();
        follow_up_data.add("1");
        follow_up_data.add("2");
        follow_up_data.add("3");
        follow_up_data.add("4");
        follow_up_data.add("5");
        follow_up_data.add("6");
        follow_up_data.add("7");
        follow_up_data.add("10");
        follow_up_data.add("15");
        follow_up_data.add("30");
        follow_up_data.add("45");
        follow_up_data.add("60");
        follow_up_data.add("90");
        follow_up_data.add("180");
        sc_follow_up.addSegments(follow_up_data);

        sc_follow_up.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    followup = follow_up_data.get(segmentViewHolder.getAbsolutePosition());
                    tv_followup.setText("in " + followup + " days");
                }
            }
        });
        btn_submit = v.findViewById(R.id.btn_submit);
        btn_print_pdf = v.findViewById(R.id.btn_print_pdf);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                MedicalCasePojo medicalCasePojo = MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo();
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getRecordReferenceId());
                jsonMedicalRecord.setFormVersion(FormVersionEnum.MFD1);
                jsonMedicalRecord.setCodeQR(MedicalCaseActivity.getMedicalCaseActivity().codeQR);
                jsonMedicalRecord.setQueueUserId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getQueueUserId());
                jsonMedicalRecord.setChiefComplain(medicalCasePojo.getSymptoms());
                jsonMedicalRecord.setPastHistory(medicalCasePojo.getPastHistory());
                jsonMedicalRecord.setFamilyHistory(medicalCasePojo.getFamilyHistory());
                jsonMedicalRecord.setKnownAllergies(medicalCasePojo.getKnownAllergies());
                jsonMedicalRecord.setClinicalFinding(medicalCasePojo.getClinicalFindings());
                jsonMedicalRecord.setProvisionalDifferentialDiagnosis(medicalCasePojo.getProvisionalDiagnosis());
                jsonMedicalRecord.setExamination(medicalCasePojo.getExaminationResults());
                jsonMedicalRecord.setDiagnosis(medicalCasePojo.getDiagnosis());
                JsonMedicalPhysical jsonMedicalPhysical = new JsonMedicalPhysical()
                        .setBloodPressure(medicalCasePojo.getBloodPressure())
                        .setPulse(medicalCasePojo.getPulse())
                        .setWeight(medicalCasePojo.getWeight())
                        .setOxygen(medicalCasePojo.getOxygenLevel())
                        .setTemperature(medicalCasePojo.getTemperature());

                if (medicalCasePojo.getPathologyList().size() > 0) {
                    ArrayList<JsonMedicalPathology> pathologies = new ArrayList<>();
                    for (int i = 0; i < medicalCasePojo.getPathologyList().size(); i++) {
                        pathologies.add(new JsonMedicalPathology().setName(medicalCasePojo.getPathologyList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalPathologies(pathologies);
                }

                ArrayList<JsonMedicalRadiology> mriList = new ArrayList<>();
                if (medicalCasePojo.getMriList().size() > 0) {
                    for (int i = 0; i < medicalCasePojo.getMriList().size(); i++) {
                        mriList.add(new JsonMedicalRadiology().setName(medicalCasePojo.getMriList().get(i)));
                    }
                }
                ArrayList<JsonMedicalRadiology> sonoList = new ArrayList<>();
                if (medicalCasePojo.getSonoList().size() > 0) {
                    for (int i = 0; i < medicalCasePojo.getSonoList().size(); i++) {
                        sonoList.add(new JsonMedicalRadiology().setName(medicalCasePojo.getSonoList().get(i)));
                    }
                }
                ArrayList<JsonMedicalRadiology> scanList = new ArrayList<>();
                if (medicalCasePojo.getScanList().size() > 0) {
                    for (int i = 0; i < medicalCasePojo.getScanList().size(); i++) {
                        scanList.add(new JsonMedicalRadiology().setName(medicalCasePojo.getScanList().get(i)));
                    }
                }
                ArrayList<JsonMedicalRadiology> xrayList = new ArrayList<>();
                if (medicalCasePojo.getXrayList().size() > 0) {
                    for (int i = 0; i < medicalCasePojo.getXrayList().size(); i++) {
                        xrayList.add(new JsonMedicalRadiology().setName(medicalCasePojo.getXrayList().get(i)));
                    }
                }
                List<JsonMedicalRadiologyList> medicalRadiologyLists = new ArrayList<>();
                if (mriList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    jsonMedicalRadiologyList.setBizStoreId("");
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.MRI);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(mriList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                if (sonoList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    jsonMedicalRadiologyList.setBizStoreId("");
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SONO);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(sonoList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                if (scanList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    jsonMedicalRadiologyList.setBizStoreId("");
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SCAN);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(scanList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                if (xrayList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    jsonMedicalRadiologyList.setBizStoreId("");
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.XRAY);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(xrayList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }

                jsonMedicalRecord.setMedicalRadiologyLists(medicalRadiologyLists);
                //  if (null != jsonPreferredBusinessList && null != jsonPreferredBusinessList.getPreferredBusinesses() && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0)
                //      jsonMedicalRecord.setStoreIdPharmacy(jsonPreferredBusinessList.getPreferredBusinesses().get(sp_preferred_list.getSelectedItemPosition()).getBizStoreId());

                jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
                jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineListWithEnum());
                jsonMedicalRecord.setPlanToPatient(medicalCasePojo.getInstructions());
                if (!TextUtils.isEmpty(followup)) {
                    jsonMedicalRecord.setFollowUpInDays(followup);
                }
                medicalHistoryModel.add(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        jsonMedicalRecord);
            }
        });
        return v;
    }

    public void updateUI() {
        final MedicalCasePojo medicalCasePojo = MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo();
        String notAvailable = "N/A";
        tv_patient_name.setText(medicalCasePojo.getName());
        tv_address.setText(medicalCasePojo.getAddress());
        tv_details.setText(medicalCasePojo.getGender() + ", " + medicalCasePojo.getAge());
        tv_symptoms.setText(medicalCasePojo.getSymptoms());
        tv_diagnosis.setText(medicalCasePojo.getDiagnosis());
        tv_instruction.setText(medicalCasePojo.getInstructions());
        tv_provisional_diagnosis.setText(medicalCasePojo.getProvisionalDiagnosis());
        tv_clinical_findings.setText(Html.fromHtml("<b>" + "Clinical Findings: " + "</b> " + medicalCasePojo.getClinicalFindings()));
        tv_examination.setText(Html.fromHtml("<b>" + "Result: " + "</b> " + medicalCasePojo.getExaminationResults()));
        tv_radio_mri.setText(covertStringList2String(medicalCasePojo.getMriList()));
        tv_radio_scan.setText(covertStringList2String(medicalCasePojo.getScanList()));
        tv_radio_sono.setText(covertStringList2String(medicalCasePojo.getSonoList()));
        tv_radio_xray.setText(covertStringList2String(medicalCasePojo.getXrayList()));
        tv_pathology.setText(covertStringList2String(medicalCasePojo.getPathologyList()));
        if (null != medicalCasePojo.getRespiratory()) {
            tv_respiratory.setText(medicalCasePojo.getRespiratory());
        } else {
            tv_respiratory.setText("Respiration Rate: " + notAvailable);
        }
        if (null != medicalCasePojo.getHeight()) {
            tv_height.setText(medicalCasePojo.getHeight());
        } else {
            tv_height.setText("Height: " + notAvailable);
        }
        if (null != medicalCasePojo.getPulse()) {
            tv_pulse.setText("Pulse: " + medicalCasePojo.getPulse());
        } else {
            tv_pulse.setText("Pulse: " + notAvailable);
        }
        if (null != medicalCasePojo.getBloodPressure() && medicalCasePojo.getBloodPressure().length == 2) {
            tv_bp.setText("Blood Pressure: " + medicalCasePojo.getBloodPressure()[0] + "/" + medicalCasePojo.getBloodPressure()[1]);
        } else {
            tv_bp.setText("Blood Pressure: " + notAvailable);
        }
        if (null != medicalCasePojo.getWeight()) {
            tv_weight.setText("Weight: " + medicalCasePojo.getWeight());
        } else {
            tv_weight.setText("Weight: " + notAvailable);
        }
        if (null != medicalCasePojo.getTemperature()) {
            tv_temperature.setText("Temp: " + medicalCasePojo.getTemperature());
        } else {
            tv_temperature.setText("Temp: " + notAvailable);
        }
        adapter = new MedicalRecordAdapter(getActivity(), medicalCasePojo.getJsonMedicineList());
        lv_medicine.setAdapter(adapter);
        btn_print_pdf.setVisibility(View.VISIBLE);
        btn_print_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfGenerator pdfGenerator = new PdfGenerator(getActivity());
                pdfGenerator.createPdf(medicalCasePojo);
            }
        });
    }


    private String covertStringList2String(ArrayList<String> data) {
        String temp = "";
        for (int i = 0; i < data.size(); i++) {
            //temp += "(" + (i + 1) + ") " + data.get(i) + "\n";
            temp += "\u2022" + " " + data.get(i) + "\n";
        }
        return temp;
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(getActivity(), "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void medicalRecordError() {
        dismissProgress();
        Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
