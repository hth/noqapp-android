package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.CustomAdapter;
import com.noqapp.android.merchant.views.adapters.MultiSelectListAdapter;
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

public class InstructionFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListView list_view;
    private TextView tv_add_instruction,tv_add_new;
    private MultiSelectListAdapter instructionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_instruction, container, false);
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
        list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        List<DataObj> DataObjList = new ArrayList<>();
        for (int i = 0; i <MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getInstructionList().size() ; i++) {
            DataObj DataObj = new DataObj();
            DataObj.setName(MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getInstructionList().get(i));
            DataObj.setSelect(false);
            DataObjList.add(DataObj);
        }
        instructionAdapter = new MultiSelectListAdapter(getActivity(),DataObjList);
        list_view.setAdapter(instructionAdapter);


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
                        ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList();
                        temp.add(new DataObj(edt_item.getText().toString(),false));
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setMedicineList(temp);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager((temp.size() / 3) + 1, LinearLayoutManager.HORIZONTAL);
                        recyclerView.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView

                        CustomAdapter customAdapter = new CustomAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getMedicineList());
                        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
                    }else {
                        ArrayList<String> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getInstructionList();
                        temp.add(edt_item.getText().toString());
                        MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setInstructionList(temp);
                        DataObj dataObj = new DataObj();
                        dataObj.setName(edt_item.getText().toString());
                        dataObj.setSelect(false);
                        instructionAdapter.addData(dataObj);
                        list_view.setAdapter(instructionAdapter);
                    }
                    Toast.makeText(getActivity(),"'"+edt_item.getText().toString()+"' added successfully to list",Toast.LENGTH_LONG).show();
                    mAlertDialog.dismiss();
                }
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        if(null != instructionAdapter)
         MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setInstructions(instructionAdapter.getAllSelectedString());
    }
}
