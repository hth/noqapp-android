package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;

public class ExaminationTabFragment extends BaseFragment {
    private AutoCompleteTextView actv_clinical_findings, actv_examination_results;
    private ProDiagnosisFragment proDiagnosisFragment;
    private DentalProDiagnosisFragment dentalDiagnosisFragment;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.frag_examination_tab, container, false);
        actv_clinical_findings = view.findViewById(R.id.actv_clinical_findings);
        actv_examination_results = view.findViewById(R.id.actv_examination_results);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (MedicalDepartmentEnum.valueOf(MedicalCaseActivity.getMedicalCaseActivity().bizCategoryId) == MedicalDepartmentEnum.DNT) {
            LinearLayout ll_examination = view.findViewById(R.id.ll_examination);
            if (LaunchActivity.isTablet) {
                FrameLayout fl_pro_diagnosis = view.findViewById(R.id.fl_pro_diagnosis);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
                fl_pro_diagnosis.setLayoutParams(lp1);
                ll_examination.setLayoutParams(lp2);
            }else{
                ll_examination.setVisibility(View.GONE);
            }

            dentalDiagnosisFragment = new DentalProDiagnosisFragment();
            getFragmentTransaction().replace(R.id.fl_pro_diagnosis, dentalDiagnosisFragment).commit();
        } else {
            proDiagnosisFragment = new ProDiagnosisFragment();
            getFragmentTransaction().replace(R.id.fl_pro_diagnosis, proDiagnosisFragment).commit();
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
