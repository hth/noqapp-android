package com.noqapp.android.merchant.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.MultiSelectListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.ShowAddDialog;

import java.util.ArrayList;
import java.util.List;

public class InstructionFragment extends BaseFragment {
    private ListView list_view;
    private TextView tv_add_instruction;
    private MultiSelectListAdapter instructionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_instruction, container, false);
        list_view = v.findViewById(R.id.list_view);
        tv_add_instruction = v.findViewById(R.id.tv_add_instruction);
        tv_add_instruction.setOnClickListener(v12 -> AddItemDialog(getActivity()));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        List<DataObj> DataObjList = new ArrayList<>();
        for (int i = 0; i < MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getInstructionList().size(); i++) {
            DataObj DataObj = new DataObj();
            DataObj.setShortName(MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getInstructionList().get(i));
            DataObj.setSelect(false);
            DataObjList.add(DataObj);
        }
        instructionAdapter = new MultiSelectListAdapter(getActivity(), DataObjList);
        list_view.setAdapter(instructionAdapter);
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getPlanToPatient()) {
                String[] temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getPlanToPatient().split("\\.");
                instructionAdapter.updateSelection(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void AddItemDialog(Context mContext) {
        ShowAddDialog showDialog = new ShowAddDialog(mContext);
        showDialog.setDialogClickListener(new ShowAddDialog.DialogClickListener() {
            @Override
            public void btnDoneClick(String str) {
                ArrayList<String> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getInstructionList();
                temp.add(str);
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setInstructionList(temp);
                DataObj dataObj = new DataObj();
                dataObj.setShortName(str);
                dataObj.setSelect(false);
                instructionAdapter.addData(dataObj);
                list_view.setAdapter(instructionAdapter);
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getInstructionList().add(str);
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                new CustomToast().showToast(getActivity(), "'" + str + "' added successfully to list");
            }
        });
        showDialog.displayDialog("Add Instruction");
    }

    public void saveData() {
        if (null != instructionAdapter)
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setInstructions(instructionAdapter.getAllSelectedString());
    }
}
