package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapterNew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import info.hoang8f.android.segmented.SegmentedGroup;

import java.util.ArrayList;

public class PrintFragment extends Fragment implements MedicalRecordPresenter {

    private TextView tv_patient_name,tv_address,tv_info,tv_symptoms,tv_diagnosis,tv_instruction,tv_radiology,tv_pathology,actv_followup,tv_clinical_findings,tv_examination,tv_provisional_diagnosis;
    private MedicalHistoryModel medicalHistoryModel;
    private Button btn_submit;
    private ListView lv_medicine;
    private MedicalRecordAdapterNew adapter;
    private SegmentedGroup rg_duration;
    private JsonPreferredBusinessList jsonPreferredBusinessList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_print, container, false);
        medicalHistoryModel = new MedicalHistoryModel(this);
        tv_patient_name = v.findViewById(R.id.tv_patient_name);
        tv_address = v.findViewById(R.id.tv_address);
        tv_info = v.findViewById(R.id.tv_info);
        tv_symptoms = v.findViewById(R.id.tv_symptoms);
        tv_diagnosis = v.findViewById(R.id.tv_diagnosis);
        tv_instruction = v.findViewById(R.id.tv_instruction);
        tv_radiology = v.findViewById(R.id.tv_radiology);
        tv_pathology = v.findViewById(R.id.tv_pathology);
        tv_clinical_findings = v.findViewById(R.id.tv_clinical_findings);
        tv_examination = v.findViewById(R.id.tv_examination);
        tv_provisional_diagnosis = v.findViewById(R.id.tv_provisional_diagnosis);
        lv_medicine = v.findViewById(R.id.lv_medicine);
        actv_followup = v.findViewById(R.id.actv_followup);
        rg_duration = v.findViewById(R.id.rg_duration);
        btn_submit = v.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getRecordReferenceId());
                jsonMedicalRecord.setFormVersion(FormVersionEnum.valueOf(BuildConfig.MEDICAL_FORM_VERSION));
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
                        .setBloodPressure(new String[]{MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getBloodPressure()})
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
                if (MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getRadiologyList().size() > 0) {
                    ArrayList<JsonMedicalRadiology> medicalRadiologies = new ArrayList<>();
                    for (int i = 0; i < MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getRadiologyList().size(); i++) {
                        medicalRadiologies.add(new JsonMedicalRadiology().setName(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getRadiologyList().get(i)));
                    }
                    jsonMedicalRecord.setMedicalRadiologies(medicalRadiologies);
                }

              //  if (null != jsonPreferredBusinessList && null != jsonPreferredBusinessList.getPreferredBusinesses() && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0)
              //      jsonMedicalRecord.setStoreIdPharmacy(jsonPreferredBusinessList.getPreferredBusinesses().get(sp_preferred_list.getSelectedItemPosition()).getBizStoreId());

                jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
                jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineListWithEnum());
                jsonMedicalRecord.setPlanToPatient(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getInstructions());
                if (!actv_followup.getText().toString().equals("")) {
                    String value = actv_followup.getText().toString();
                    int selectedId = rg_duration.getCheckedRadioButtonId();
                    if (selectedId == R.id.rb_months) {
                        jsonMedicalRecord.setFollowUpInDays(String.valueOf(Integer.parseInt(value) * 30));
                    } else {
                        jsonMedicalRecord.setFollowUpInDays(value);
                    }
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
        tv_radiology.setText(covertStringList2String(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getRadiologyList()));
        tv_pathology.setText(covertStringList2String(MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getPathologyList()));
        adapter = new MedicalRecordAdapterNew(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().getJsonMedicineList());
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
        if (1 == jsonResponse.getResponse()) {
            Toast.makeText(getActivity(), "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void medicalRecordError() {
        Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
    }
}
