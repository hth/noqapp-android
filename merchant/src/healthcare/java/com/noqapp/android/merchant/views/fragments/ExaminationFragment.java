package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;


public class ExaminationFragment extends Fragment {

    private AutoCompleteTextView actv_clinical_findings,actv_examination_results;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_examination, container, false);
        actv_clinical_findings = v.findViewById(R.id.actv_clinical_findings);
        actv_examination_results = v.findViewById(R.id.actv_examination_results);
        return v;
    }


    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setClinicalFindings(actv_clinical_findings.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setExaminationResults(actv_examination_results.getText().toString());
    }


}
