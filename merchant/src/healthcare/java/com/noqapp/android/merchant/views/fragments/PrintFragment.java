package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiologyList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.DurationDaysEnum;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomSpinnerAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.merchant.views.pojos.CaseHistory;
import com.noqapp.android.merchant.views.utils.PdfGenerator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private TextView tv_radio_xray, tv_radio_sono, tv_radio_scan, tv_radio_mri, tv_radio_special, tv_details, tv_followup;
    private TextView tv_weight, tv_height, tv_respiratory, tv_temperature, tv_bp, tv_pulse;
    private MedicalHistoryModel medicalHistoryModel;
    private SegmentedControl sc_follow_up;
    private Button btn_submit, btn_print_pdf;
    private ListView lv_medicine;
    private MedicalRecordAdapter adapter;
    private JsonPreferredBusinessList jsonPreferredBusinessList;
    private ProgressDialog progressDialog;
    private ArrayList<String> follow_up_data = new ArrayList<>();
    private String followup;
    private LinearLayout ll_sono, ll_scan, ll_mri, ll_xray, ll_spec, ll_path;
    private AppCompatSpinner acsp_mri, acsp_scan, acsp_sono,
            acsp_xray, acsp_special, acsp_pathology, acsp_pharmacy;

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
        tv_radio_special = v.findViewById(R.id.tv_radio_special);
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
        acsp_mri = v.findViewById(R.id.acsp_mri);
        acsp_scan = v.findViewById(R.id.acsp_scan);
        acsp_sono = v.findViewById(R.id.acsp_sono);
        acsp_xray = v.findViewById(R.id.acsp_xray);
        acsp_special = v.findViewById(R.id.acsp_special);
        acsp_pathology = v.findViewById(R.id.acsp_pathology);
        acsp_pharmacy = v.findViewById(R.id.acsp_pharmacy);

        ll_sono = v.findViewById(R.id.ll_sono);
        ll_scan = v.findViewById(R.id.ll_scan);
        ll_mri = v.findViewById(R.id.ll_mri);
        ll_xray = v.findViewById(R.id.ll_xray);
        ll_spec = v.findViewById(R.id.ll_spec);
        ll_path = v.findViewById(R.id.ll_path);

        sc_follow_up = v.findViewById(R.id.sc_follow_up);
        follow_up_data.clear();
        follow_up_data.add(String.valueOf(DurationDaysEnum.D1D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D2D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D3D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D4D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D5D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D6D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D7D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D10D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D15D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D1M.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D45D.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D2M.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D3M.getValue()));
        follow_up_data.add(String.valueOf(DurationDaysEnum.D6M.getValue()));
        sc_follow_up.addSegments(follow_up_data);

        sc_follow_up.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    followup = follow_up_data.get(segmentViewHolder.getAbsolutePosition());
                    tv_followup.setText("in " + followup + " days");
                    MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setFollowup(tv_followup.getText().toString());

                }
            }
        });
        btn_submit = v.findViewById(R.id.btn_submit);
        btn_print_pdf = v.findViewById(R.id.btn_print_pdf);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                CaseHistory caseHistory = MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory();
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getRecordReferenceId());
                jsonMedicalRecord.setFormVersion(FormVersionEnum.MFD1);
                jsonMedicalRecord.setCodeQR(MedicalCaseActivity.getMedicalCaseActivity().codeQR);
                jsonMedicalRecord.setQueueUserId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getQueueUserId());
                jsonMedicalRecord.setChiefComplain(caseHistory.getSymptomsObject());
                jsonMedicalRecord.getJsonUserMedicalProfile().setPastHistory(caseHistory.getPastHistory());
                jsonMedicalRecord.getJsonUserMedicalProfile().setFamilyHistory(caseHistory.getFamilyHistory());
                jsonMedicalRecord.getJsonUserMedicalProfile().setKnownAllergies(caseHistory.getKnownAllergies());
                jsonMedicalRecord.getJsonUserMedicalProfile().setMedicineAllergies(caseHistory.getMedicineAllergies());
                jsonMedicalRecord.getJsonUserMedicalProfile().setHistoryDirty(caseHistory.isHistoryFilled());
                jsonMedicalRecord.setClinicalFinding(caseHistory.getClinicalFindings());
                jsonMedicalRecord.setProvisionalDifferentialDiagnosis(caseHistory.getProvisionalDiagnosis());
                jsonMedicalRecord.setExamination(caseHistory.getExaminationResults());
                jsonMedicalRecord.setDiagnosis(caseHistory.getDiagnosis());
                JsonMedicalPhysical jsonMedicalPhysical = new JsonMedicalPhysical()
                        .setBloodPressure(caseHistory.getBloodPressure())
                        .setPulse(caseHistory.getPulse())
                        .setWeight(caseHistory.getWeight())
                        .setOxygen(caseHistory.getOxygenLevel())
                        .setTemperature(caseHistory.getTemperature())
                        .setHeight(caseHistory.getHeight())
                        .setRespiratory(caseHistory.getRespiratory())
                        .setPhysicalFilled(caseHistory.isPhysicalFilled());

                if (caseHistory.getPathologyList().size() > 0) {
                    ArrayList<JsonMedicalPathology> pathologies = new ArrayList<>();
                    for (int i = 0; i < caseHistory.getPathologyList().size(); i++) {
                        pathologies.add(new JsonMedicalPathology().setName(caseHistory.getPathologyList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalPathologies(pathologies);
                }

                ArrayList<JsonMedicalRadiology> mriList = new ArrayList<>();
                if (caseHistory.getMriList().size() > 0) {
                    for (int i = 0; i < caseHistory.getMriList().size(); i++) {
                        mriList.add(new JsonMedicalRadiology().setName(caseHistory.getMriList().get(i)));
                    }
                }
                ArrayList<JsonMedicalRadiology> sonoList = new ArrayList<>();
                if (caseHistory.getSonoList().size() > 0) {
                    for (int i = 0; i < caseHistory.getSonoList().size(); i++) {
                        sonoList.add(new JsonMedicalRadiology().setName(caseHistory.getSonoList().get(i)));
                    }
                }
                ArrayList<JsonMedicalRadiology> scanList = new ArrayList<>();
                if (caseHistory.getScanList().size() > 0) {
                    for (int i = 0; i < caseHistory.getScanList().size(); i++) {
                        scanList.add(new JsonMedicalRadiology().setName(caseHistory.getScanList().get(i)));
                    }
                }
                ArrayList<JsonMedicalRadiology> xrayList = new ArrayList<>();
                if (caseHistory.getXrayList().size() > 0) {
                    for (int i = 0; i < caseHistory.getXrayList().size(); i++) {
                        xrayList.add(new JsonMedicalRadiology().setName(caseHistory.getXrayList().get(i)));
                    }
                }

                ArrayList<JsonMedicalRadiology> specList = new ArrayList<>();
                if (caseHistory.getSpecList().size() > 0) {
                    for (int i = 0; i < caseHistory.getSpecList().size(); i++) {
                        specList.add(new JsonMedicalRadiology().setName(caseHistory.getSpecList().get(i)));
                    }
                }

                boolean isPreferedBusinessAvailable = false;
                if (null != jsonPreferredBusinessList && null != jsonPreferredBusinessList.getPreferredBusinesses() && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0)
                    isPreferedBusinessAvailable = true;
                List<JsonMedicalRadiologyList> medicalRadiologyLists = new ArrayList<>();
                if (mriList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    if (isPreferedBusinessAvailable) {
                        if (acsp_mri.getSelectedItemPosition() != 0) {
                            jsonMedicalRadiologyList.setBizStoreId(jsonPreferredBusinessList.getPreferredBusinesses().get(acsp_mri.getSelectedItemPosition()).getBizStoreId());
                        } else {
                            jsonMedicalRadiologyList.setBizStoreId("");
                        }

                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.MRI);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(mriList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                if (sonoList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    if (isPreferedBusinessAvailable) {
                        if (acsp_sono.getSelectedItemPosition() != 0) {
                            jsonMedicalRadiologyList.setBizStoreId(jsonPreferredBusinessList.getPreferredBusinesses().get(acsp_sono.getSelectedItemPosition()).getBizStoreId());
                        } else {
                            jsonMedicalRadiologyList.setBizStoreId("");
                        }
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SONO);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(sonoList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                if (scanList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    if (isPreferedBusinessAvailable) {
                        if (acsp_scan.getSelectedItemPosition() != 0) {
                            jsonMedicalRadiologyList.setBizStoreId(jsonPreferredBusinessList.getPreferredBusinesses().get(acsp_scan.getSelectedItemPosition()).getBizStoreId());
                        } else {
                            jsonMedicalRadiologyList.setBizStoreId("");
                        }
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SCAN);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(scanList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                if (xrayList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    if (isPreferedBusinessAvailable) {
                        if (acsp_xray.getSelectedItemPosition() != 0) {
                            jsonMedicalRadiologyList.setBizStoreId(jsonPreferredBusinessList.getPreferredBusinesses().get(acsp_xray.getSelectedItemPosition()).getBizStoreId());
                        } else {
                            jsonMedicalRadiologyList.setBizStoreId("");
                        }
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.XRAY);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(xrayList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                if (specList.size() > 0) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList();
                    if (isPreferedBusinessAvailable) {
                        if (acsp_special.getSelectedItemPosition() != 0) {
                            jsonMedicalRadiologyList.setBizStoreId(jsonPreferredBusinessList.getPreferredBusinesses().get(acsp_special.getSelectedItemPosition()).getBizStoreId());
                        } else {
                            jsonMedicalRadiologyList.setBizStoreId("");
                        }
                    } else {
                        jsonMedicalRadiologyList.setBizStoreId("");
                    }
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.SPEC);
                    jsonMedicalRadiologyList.setJsonMedicalRadiologies(specList);
                    medicalRadiologyLists.add(jsonMedicalRadiologyList);
                }
                jsonMedicalRecord.setMedicalRadiologyLists(medicalRadiologyLists);
                if (isPreferedBusinessAvailable) {
                    if (acsp_pharmacy.getSelectedItemPosition() != 0) {
                        jsonMedicalRecord.setStoreIdPharmacy(jsonPreferredBusinessList.getPreferredBusinesses().get(acsp_pharmacy.getSelectedItemPosition()).getBizStoreId());
                    } else {
                        jsonMedicalRecord.setStoreIdPharmacy("");
                    }
                    if (acsp_pathology.getSelectedItemPosition() != 0) {
                        jsonMedicalRecord.setStoreIdPathology(jsonPreferredBusinessList.getPreferredBusinesses().get(acsp_pathology.getSelectedItemPosition()).getBizStoreId());
                    } else {
                        jsonMedicalRecord.setStoreIdPathology("");
                    }
                } else {
                    jsonMedicalRecord.setStoreIdPharmacy("");
                    jsonMedicalRecord.setStoreIdPathology("");
                }
                jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
                jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineListWithEnum());
                jsonMedicalRecord.setPlanToPatient(caseHistory.getInstructions());
                if (!TextUtils.isEmpty(followup)) {
                    jsonMedicalRecord.setFollowUpInDays(followup);
                }
                medicalHistoryModel.update(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        jsonMedicalRecord);
            }
        });
        return v;
    }

    public void updateUI() {
        final CaseHistory caseHistory = MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory();
        caseHistory.setFollowup(tv_followup.getText().toString());
        String notAvailable = "N/A";
        tv_patient_name.setText(caseHistory.getName());
        tv_address.setText(caseHistory.getAddress());
        tv_details.setText(caseHistory.getGender() + ", " + caseHistory.getAge());
        tv_symptoms.setText(caseHistory.getSymptoms());
        tv_diagnosis.setText(caseHistory.getDiagnosis());
        tv_instruction.setText(caseHistory.getInstructions());
        tv_provisional_diagnosis.setText(caseHistory.getProvisionalDiagnosis());
        tv_clinical_findings.setText(Html.fromHtml("<b>" + "Clinical Findings: " + "</b> " + caseHistory.getClinicalFindings()));
        tv_examination.setText(Html.fromHtml("<b>" + "Result: " + "</b> " + caseHistory.getExaminationResults()));
        tv_radio_mri.setText(covertStringList2String(caseHistory.getMriList()));
        tv_radio_scan.setText(covertStringList2String(caseHistory.getScanList()));
        tv_radio_sono.setText(covertStringList2String(caseHistory.getSonoList()));
        tv_radio_xray.setText(covertStringList2String(caseHistory.getXrayList()));
        tv_radio_special.setText(covertStringList2String(caseHistory.getSpecList()));
        tv_pathology.setText(covertStringList2String(caseHistory.getPathologyList()));
        hideInvestigationViews(caseHistory);
        if (null != caseHistory.getRespiratory()) {
            tv_respiratory.setText(caseHistory.getRespiratory());
        } else {
            tv_respiratory.setText("Respiration Rate: " + notAvailable);
        }
        if (null != caseHistory.getHeight()) {
            tv_height.setText(caseHistory.getHeight());
        } else {
            tv_height.setText("Height: " + notAvailable);
        }
        if (null != caseHistory.getPulse()) {
            tv_pulse.setText("Pulse: " + caseHistory.getPulse());
        } else {
            tv_pulse.setText("Pulse: " + notAvailable);
        }
        if (null != caseHistory.getBloodPressure() && caseHistory.getBloodPressure().length == 2) {
            tv_bp.setText("Blood Pressure: " + caseHistory.getBloodPressure()[0] + "/" + caseHistory.getBloodPressure()[1]);
        } else {
            tv_bp.setText("Blood Pressure: " + notAvailable);
        }
        if (null != caseHistory.getWeight()) {
            tv_weight.setText("Weight: " + caseHistory.getWeight());
        } else {
            tv_weight.setText("Weight: " + notAvailable);
        }
        if (null != caseHistory.getTemperature()) {
            tv_temperature.setText("Temp: " + caseHistory.getTemperature());
        } else {
            tv_temperature.setText("Temp: " + notAvailable);
        }
        adapter = new MedicalRecordAdapter(getActivity(), caseHistory.getJsonMedicineList());
        lv_medicine.setAdapter(adapter);
        btn_print_pdf.setVisibility(View.VISIBLE);
        btn_print_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfGenerator pdfGenerator = new PdfGenerator(getActivity());
                pdfGenerator.createPdf(caseHistory, TextUtils.isEmpty(followup) ? 0 : Integer.parseInt(followup));
            }
        });

        if (!TextUtils.isEmpty(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getFollowUpInDays())) {
            int index = follow_up_data.indexOf(MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getFollowUpInDays());
            if (-1 != index)
                sc_follow_up.setSelectedSegment(index);
        }

        jsonPreferredBusinessList = MedicalCaseActivity.getMedicalCaseActivity().jsonPreferredBusinessList;
        jsonPreferredBusinessList.getPreferredBusinesses().add(0, new JsonPreferredBusiness().setDisplayName("Select"));
        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(getActivity(), jsonPreferredBusinessList.getPreferredBusinesses());
        acsp_mri.setAdapter(spinAdapter);
        acsp_scan.setAdapter(spinAdapter);
        acsp_sono.setAdapter(spinAdapter);
        acsp_xray.setAdapter(spinAdapter);
        acsp_special.setAdapter(spinAdapter);
        acsp_pathology.setAdapter(spinAdapter);
        acsp_pharmacy.setAdapter(spinAdapter);
    }

    private void hideInvestigationViews(CaseHistory caseHistory) {
        ll_mri.setVisibility(caseHistory.getMriList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_scan.setVisibility(caseHistory.getScanList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_sono.setVisibility(caseHistory.getSonoList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_xray.setVisibility(caseHistory.getXrayList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_spec.setVisibility(caseHistory.getSpecList().size() == 0 ? View.GONE : View.VISIBLE);
        ll_path.setVisibility(caseHistory.getPathologyList().size() == 0 ? View.GONE : View.VISIBLE);
    }


    private String covertStringList2String(List<String> data) {
        String temp = "";
        for (String a : data) {
            temp += "\u2022" + " " + a + "\n";
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
