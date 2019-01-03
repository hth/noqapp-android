package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
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
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class PrintFragment extends Fragment implements MedicalRecordPresenter {

    private TextView tv_patient_name,tv_address,tv_info,tv_symptoms,tv_diagnosis,tv_instruction,tv_pathology,tv_clinical_findings,tv_examination,tv_provisional_diagnosis;
    private TextView tv_radio_xray,tv_radio_sono,tv_radio_scan,tv_radio_mri;
    private MedicalHistoryModel medicalHistoryModel;
    private SegmentedControl sc_follow_up;
    private Button btn_submit;
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
        tv_info = v.findViewById(R.id.tv_info);
        tv_symptoms = v.findViewById(R.id.tv_symptoms);
        tv_diagnosis = v.findViewById(R.id.tv_diagnosis);
        tv_instruction = v.findViewById(R.id.tv_instruction);
        tv_radio_mri = v.findViewById(R.id.tv_radio_mri);
        tv_radio_scan = v.findViewById(R.id.tv_radio_scan);
        tv_radio_sono = v.findViewById(R.id.tv_radio_sono);
        tv_radio_xray = v.findViewById(R.id.tv_radio_xray);
        tv_pathology = v.findViewById(R.id.tv_pathology);
        tv_clinical_findings = v.findViewById(R.id.tv_clinical_findings);
        tv_examination = v.findViewById(R.id.tv_examination);
        tv_provisional_diagnosis = v.findViewById(R.id.tv_provisional_diagnosis);
        lv_medicine = v.findViewById(R.id.lv_medicine);
        sc_follow_up = v.findViewById(R.id.sc_follow_up);

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
                }
            }
        });
        btn_submit = v.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getRecordReferenceId());
                jsonMedicalRecord.setFormVersion(FormVersionEnum.MFD1);
                jsonMedicalRecord.setCodeQR(MedicalCaseActivity.getMedicalCaseActivity().codeQR);
                jsonMedicalRecord.setQueueUserId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getQueueUserId());
                jsonMedicalRecord.setChiefComplain(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getSymptoms());
                jsonMedicalRecord.setPastHistory(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getPastHistory());
                jsonMedicalRecord.setFamilyHistory(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getFamilyHistory());
                jsonMedicalRecord.setKnownAllergies(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getKnownAllergies());
                jsonMedicalRecord.setClinicalFinding(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getClinicalFindings());
                jsonMedicalRecord.setProvisionalDifferentialDiagnosis(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getProvisionalDiagnosis());
                jsonMedicalRecord.setExamination(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getExaminationResults());
                jsonMedicalRecord.setDiagnosis(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getDiagnosis());
                JsonMedicalPhysical jsonMedicalPhysical = new JsonMedicalPhysical()
                        .setBloodPressure(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getBloodPressure())
                        .setPluse(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getPulse())
                        .setWeight(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getWeight())
                        .setOxygen(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getOxygenLevel())
                        .setTemperature(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getTemperature());

                if (MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getPathologyList().size() > 0) {
                    ArrayList<JsonMedicalPathology> pathologies = new ArrayList<>();
                    for (int i = 0; i < MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getPathologyList().size(); i++) {
                        pathologies.add(new JsonMedicalPathology().setName(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getPathologyList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalPathologies(pathologies);
                }
                if (MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getMriList().size() > 0) {
                    ArrayList<JsonMedicalRadiology> medicalRadiologies = new ArrayList<>();
                    for (int i = 0; i < MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getMriList().size(); i++) {
                        medicalRadiologies.add(new JsonMedicalRadiology().setName(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getMriList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalRadiologies(medicalRadiologies);
                }
                if (MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getSonoList().size() > 0) {
                    ArrayList<JsonMedicalRadiology> medicalRadiologies = new ArrayList<>();
                    for (int i = 0; i < MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getSonoList().size(); i++) {
                        medicalRadiologies.add(new JsonMedicalRadiology().setName(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getSonoList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalRadiologies(medicalRadiologies);
                }
                if (MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getScanList().size() > 0) {
                    ArrayList<JsonMedicalRadiology> medicalRadiologies = new ArrayList<>();
                    for (int i = 0; i < MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getScanList().size(); i++) {
                        medicalRadiologies.add(new JsonMedicalRadiology().setName(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getScanList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalRadiologies(medicalRadiologies);
                }
                if (MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getXrayList().size() > 0) {
                    ArrayList<JsonMedicalRadiology> medicalRadiologies = new ArrayList<>();
                    for (int i = 0; i < MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getXrayList().size(); i++) {
                        medicalRadiologies.add(new JsonMedicalRadiology().setName(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getXrayList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalRadiologies(medicalRadiologies);
                }

              //  if (null != jsonPreferredBusinessList && null != jsonPreferredBusinessList.getPreferredBusinesses() && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0)
              //      jsonMedicalRecord.setStoreIdPharmacy(jsonPreferredBusinessList.getPreferredBusinesses().get(sp_preferred_list.getSelectedItemPosition()).getBizStoreId());

                jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
                jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineListWithEnum());
                jsonMedicalRecord.setPlanToPatient(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getInstructions());
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
        tv_patient_name.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getName());
        tv_address.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getAddress());
        tv_info.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getDetails());
        tv_symptoms.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getSymptoms());
        tv_diagnosis.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getDiagnosis());
        tv_instruction.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getInstructions());
        tv_provisional_diagnosis.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getProvisionalDiagnosis());
        tv_clinical_findings.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getClinicalFindings());
        tv_examination.setText(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getExaminationResults());
        tv_radio_mri.setText(covertStringList2String(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getMriList()));
        tv_radio_scan.setText(covertStringList2String(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getScanList()));
        tv_radio_sono.setText(covertStringList2String(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getSonoList()));
        tv_radio_xray.setText(covertStringList2String(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getXrayList()));
        tv_pathology.setText(covertStringList2String(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getPathologyList()));
        adapter = new MedicalRecordAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getJsonMedicineList());
        lv_medicine.setAdapter(adapter);
    }


    private String covertStringList2String(ArrayList<String> data){
        String temp = "";
        for (int i = 0; i < data.size(); i++) {
           temp += "("+(i+1)+") "+data.get(i)+"\n" ;
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
