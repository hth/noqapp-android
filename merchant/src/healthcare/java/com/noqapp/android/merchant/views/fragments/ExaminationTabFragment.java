package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;

public class ExaminationTabFragment extends BaseFragment {
    private AutoCompleteTextView actv_clinical_findings, actv_examination_results;
    private ProDiagnosisFragment proDiagnosisFragment;
    private DentalProDiagnosisFragment dentalDiagnosisFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_examination_tab, container, false);
        actv_clinical_findings = v.findViewById(R.id.actv_clinical_findings);
        actv_examination_results = v.findViewById(R.id.actv_examination_results);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (MedicalDepartmentEnum.valueOf(MedicalCaseActivity.getMedicalCaseActivity().bizCategoryId) == MedicalDepartmentEnum.DNT) {
            dentalDiagnosisFragment = new DentalProDiagnosisFragment();
            transaction.replace(R.id.fl_pro_diagnosis, dentalDiagnosisFragment).commit();
        } else {
            proDiagnosisFragment = new ProDiagnosisFragment();
            transaction.replace(R.id.fl_pro_diagnosis, proDiagnosisFragment).commit();
        }
        JsonMedicalRecord jsonMedicalRecord = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord();
        if (null != jsonMedicalRecord) {
            actv_clinical_findings.setText(jsonMedicalRecord.getClinicalFinding() == null ? "" : jsonMedicalRecord.getClinicalFinding());
            actv_examination_results.setText(jsonMedicalRecord.getExamination() == null ? "" : jsonMedicalRecord.getExamination());
        }
    }

    public void saveData() {
        if (null != proDiagnosisFragment)
            proDiagnosisFragment.saveData();
        if (null != dentalDiagnosisFragment)
            dentalDiagnosisFragment.saveData();
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setClinicalFindings(actv_clinical_findings.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setExaminationResults(actv_examination_results.getText().toString());
    }
}
