package com.noqapp.android.merchant.views.fragments;


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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.DataObj;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomAdapter;
import java.util.ArrayList;

public class TreatmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListView list_view;
    private TextView tv_add_instruction,tv_add_new;
    private CustomAdapter medicineAdapter;
    private ArrayAdapter<String> instructionAdapter;
    private String instruction = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_treatment, container, false);
        // get the reference of RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);
        list_view = v.findViewById(R.id.list_view);
        tv_add_instruction = v.findViewById(R.id.tv_add_instruction);
        tv_add_new = v.findViewById(R.id.tv_add_new);
        tv_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(),"Add Medicine",true);
            }
        });
        tv_add_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog(getActivity(),"Add Instruction",false);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((MedicalCaseActivity.getMedicalCaseActivity().getMedicineList().size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        medicineAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getMedicineList());
        recyclerView.setAdapter(medicineAdapter); // set the Adapter to RecyclerView

        instructionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, MedicalCaseActivity.getMedicalCaseActivity().getInstructionList());
        list_view.setAdapter(instructionAdapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                instruction = instructionAdapter.getItem(position);
            }
        });
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
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().getMedicineList();
                        temp.add(new DataObj(edt_item.getText().toString(),false));
                        MedicalCaseActivity.getMedicalCaseActivity().setMedicineList(temp);
                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().getMedicineList());
                        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
                    }else {
                        ArrayList<String> temp = MedicalCaseActivity.getMedicalCaseActivity().getInstructionList();
                        temp.add(edt_item.getText().toString());
                        MedicalCaseActivity.getMedicalCaseActivity().setInstructionList(temp);
                        ArrayAdapter<String> zooAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, MedicalCaseActivity.getMedicalCaseActivity().getInstructionList());
                        list_view.setAdapter(zooAdapter);
                    }
                    Toast.makeText(getActivity(),"'"+edt_item.getText().toString()+"' added successfully to list",Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setJsonMedicineList(medicineAdapter.getSelectedDataList());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setInstructions(instruction);
    }
}
