package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DiagnosisFragment extends Fragment {

    private RecyclerView rcv_provisional_diagnosis;
    private StaggeredGridAdapter provisionalDiagnosisAdapter;
    private AutoCompleteTextView actv_clinical_findings,actv_examination_results;
    private final int columnCount = 4;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_diagnosis, container, false);
        actv_clinical_findings = v.findViewById(R.id.actv_clinical_findings);
        actv_examination_results = v.findViewById(R.id.actv_examination_results);
        TextView tv_add_provisional_diagnosis = v.findViewById(R.id.tv_add_provisional_diagnosis);
        rcv_provisional_diagnosis = v.findViewById(R.id.rcv_provisional_diagnosis);
        tv_add_provisional_diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(), "Add Provisional Diagnosis", true);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rcv_provisional_diagnosis.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        provisionalDiagnosisAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList());
        rcv_provisional_diagnosis.setAdapter(provisionalDiagnosisAdapter);

        JsonMedicalRecord jsonMedicalRecord = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord();
        actv_clinical_findings.setText(jsonMedicalRecord.getClinicalFinding());
        actv_examination_results.setText(jsonMedicalRecord.getExamination());

        try {
            String[] temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getProvisionalDifferentialDiagnosis().split(",");
            provisionalDiagnosisAdapter.updateSelection(temp);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void AddItemDialog(final Context mContext, String title, final boolean isMedicine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.add_item, null, false);
        final EditText edt_item = customDialogView.findViewById(R.id.edt_item);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        tvtitle.setText(title);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button btn_cancel = customDialogView.findViewById(R.id.btn_cancel);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edt_item.setError(null);
                if (edt_item.getText().toString().equals("")) {
                    edt_item.setError("Empty field not allowed");
                } else {
                    if (isMedicine) {
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList();
                        temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setProvisionalDiagnosisList(temp);
                         rcv_provisional_diagnosis.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                        provisionalDiagnosisAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList());
                        rcv_provisional_diagnosis.setAdapter(provisionalDiagnosisAdapter);
                        MedicalCaseActivity.getMedicalCaseActivity().getTestCaseObjects().getProDiagnosisList().add(new DataObj(edt_item.getText().toString(), false));
                        MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                    } else {

                    }
                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setProvisionalDiagnosis(provisionalDiagnosisAdapter.getSelectedData());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setClinicalFindings(actv_clinical_findings.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setExaminationResults(actv_examination_results.getText().toString());
    }
}
