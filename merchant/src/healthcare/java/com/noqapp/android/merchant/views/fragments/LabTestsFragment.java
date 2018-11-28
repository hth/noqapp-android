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

public class LabTestsFragment extends Fragment {

    private RecyclerView recyclerView, recyclerView_one;
    private TextView tv_add_dia,tv_add_new;
    private CustomAdapter radiologyAdapter, pathalogyAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_recomand, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView_one = v.findViewById(R.id.recyclerView_one);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_dia = v.findViewById(R.id.tv_add_dia);
        tv_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(),"Add Radiology",true);
            }
        });
        tv_add_dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(),"Add Pathology",false);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set a StaggeredGridLayoutManager with 3 number of columns and horizontal orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getRadiologyList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        radiologyAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getRadiologyList());
        recyclerView.setAdapter(radiologyAdapter); // set the Adapter to RecyclerView


        // set a StaggeredGridLayoutManager with 3 number of columns and horizontal orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView_one.setLayoutManager(staggeredGridLayoutManager1); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        pathalogyAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList());
        recyclerView_one.setAdapter(pathalogyAdapter); // set the Adapter to RecyclerView
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
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getRadiologyList();
                        temp.add(new DataObj(edt_item.getText().toString(),false));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setRadiologyList(temp);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                        recyclerView.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView

                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getRadiologyList());
                        recyclerView.setAdapter(customAdapter);
                    }else {
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList();
                        temp.add(new DataObj(edt_item.getText().toString(),false));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setPathologyList(temp);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                        recyclerView_one.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView

                        CustomAdapter customAdapter1 = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getPathologyList());
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
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setRadiologyList(radiologyAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setPathologyList(pathalogyAdapter.getSelectedDataList());
    }
}
