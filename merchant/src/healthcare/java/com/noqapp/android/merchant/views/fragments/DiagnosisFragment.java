package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DiagnosisFragment extends Fragment {

    private RecyclerView rcv_provisional_diagnosis;
    private CustomAdapter provisionalDiagnosisAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_diagnosis, container, false);
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
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_provisional_diagnosis.setLayoutManager(staggeredGridLayoutManager);
        provisionalDiagnosisAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList());
        rcv_provisional_diagnosis.setAdapter(provisionalDiagnosisAdapter);

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
                        temp.add(new DataObj(edt_item.getText().toString(), false));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setProvisionalDiagnosisList(temp);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                        rcv_provisional_diagnosis.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView

                        provisionalDiagnosisAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList());
                        rcv_provisional_diagnosis.setAdapter(provisionalDiagnosisAdapter);
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
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setProvisionalDiagnosis(provisionalDiagnosisAdapter.getSelectedData());

    }
}
