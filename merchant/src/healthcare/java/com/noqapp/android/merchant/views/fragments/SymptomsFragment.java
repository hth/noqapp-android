package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.DataObj;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomAdapter;

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

public class SymptomsFragment extends Fragment {

    private RecyclerView recyclerView, recyclerView_one;
    private TextView tv_add_dia,tv_add_new;
    private CustomAdapter symptomsAdapter, diagnosisAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_symptoms, container, false);
        // get the reference of RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView_one = v.findViewById(R.id.recyclerView_one);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_dia = v.findViewById(R.id.tv_add_dia);
        tv_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(),"Add Symptoms",true);
            }
        });
        tv_add_dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(),"Add Diagnosis",false);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // set a StaggeredGridLayoutManager with 3 number of columns and horizontal orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().getSymptomsList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        symptomsAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getSymptomsList());
        recyclerView.setAdapter(symptomsAdapter); // set the Adapter to RecyclerView

        // set a StaggeredGridLayoutManager with 3 number of columns and horizontal orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().getDiagnosisList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView_one.setLayoutManager(staggeredGridLayoutManager1); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        diagnosisAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getDiagnosisList());
        recyclerView_one.setAdapter(diagnosisAdapter); // set the Adapter to RecyclerView
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
                    if(isMedicine){
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().getSymptomsList();
                        temp.add(new DataObj(edt_item.getText().toString(),false));
                        MedicalCaseActivity.getMedicalCaseActivity().setSymptomsList(temp);
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getSymptomsList());
                        recyclerView.setAdapter(customAdapter);
                    }else {
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().getDiagnosisList();
                        temp.add(new DataObj(edt_item.getText().toString(),false));
                        MedicalCaseActivity.getMedicalCaseActivity().setDiagnosisList(temp);
                        CustomAdapter customAdapter1 = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getDiagnosisList());
                        recyclerView_one.setAdapter(customAdapter1);
                    }
                    Toast.makeText(getActivity(),"'"+edt_item.getText().toString()+"' added successfully to list",Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setSymptoms(symptomsAdapter.getSelectedData());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setDiagnosis(diagnosisAdapter.getSelectedData());
    }


}
