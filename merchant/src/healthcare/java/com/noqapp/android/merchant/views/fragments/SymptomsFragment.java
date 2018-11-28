package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalHistoryAdapter;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class SymptomsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tv_add_new;
    private CustomAdapter symptomsAdapter;
    private ListView listview;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_symptoms, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        listview = v.findViewById(R.id.listview);
        tv_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(),"Add Symptoms");
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        symptomsAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList());
        recyclerView.setAdapter(symptomsAdapter);
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
                    symptomsAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getSymptomsList());
                    recyclerView.setAdapter(symptomsAdapter);
                    Toast.makeText(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list", Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setSymptoms(symptomsAdapter.getSelectedData());
    }

    public void updateList(List<JsonMedicalRecord> jsonMedicalRecords){
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(getActivity(), jsonMedicalRecords);
        listview.setAdapter(adapter);
    }

}
