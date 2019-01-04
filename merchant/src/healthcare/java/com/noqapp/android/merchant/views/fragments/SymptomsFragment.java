package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
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

public class SymptomsFragment extends Fragment {

    private RecyclerView recyclerView, rcv_obstretics;
    private TextView tv_add_new;
    private StaggeredGridAdapter symptomsAdapter, obstreticsAdapter;
    private AutoCompleteTextView actv_past_history, actv_known_allergy, actv_family_history;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_symptoms, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        rcv_obstretics = v.findViewById(R.id.rcv_obstretics);
        actv_past_history = v.findViewById(R.id.actv_past_history);
        actv_family_history = v.findViewById(R.id.actv_family_history);
        actv_known_allergy = v.findViewById(R.id.actv_known_allergy);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(), "Add Symptoms");
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        symptomsAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList());
        recyclerView.setAdapter(symptomsAdapter);


        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        rcv_obstretics.setLayoutManager(staggeredGridLayoutManager1);
        obstreticsAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getObstreticsList());
        rcv_obstretics.setAdapter(obstreticsAdapter);
    }

    private void AddItemDialog(final Context mContext, String title) {
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

                    ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList();
                    temp.add(new DataObj(edt_item.getText().toString(), false));
                    MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setSymptomsList(temp);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView
                    symptomsAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList());
                    recyclerView.setAdapter(symptomsAdapter);
                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setKnownAllergies(actv_known_allergy.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setPastHistory(actv_past_history.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setFamilyHistory(actv_family_history.getText().toString());
        String temp = obstreticsAdapter.getSelectedData();
        if (TextUtils.isEmpty(temp))
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setSymptoms(symptomsAdapter.getSelectedData());
        else
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setSymptoms(symptomsAdapter.getSelectedData() + "," + obstreticsAdapter.getSelectedData());
    }


}
