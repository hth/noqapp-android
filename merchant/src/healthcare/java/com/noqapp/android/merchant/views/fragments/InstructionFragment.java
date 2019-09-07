package com.noqapp.android.merchant.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.MultiSelectListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

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
        tv_add_instruction.setOnClickListener(v12 -> AddItemDialog(getActivity(), "Add Instruction"));
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
        ImageView iv_close = customDialogView.findViewById(R.id.iv_close);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        iv_close.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_add.setOnClickListener(v -> {
            edt_item.setError(null);
            if (edt_item.getText().toString().equals("")) {
                edt_item.setError("Empty field not allowed");
            } else {
                ArrayList<String> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getInstructionList();
                temp.add(edt_item.getText().toString());
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setInstructionList(temp);
                DataObj dataObj = new DataObj();
                dataObj.setShortName(edt_item.getText().toString());
                dataObj.setSelect(false);
                instructionAdapter.addData(dataObj);
                list_view.setAdapter(instructionAdapter);
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getInstructionList().add(edt_item.getText().toString());
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                new CustomToast().showToast(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list");
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    public void saveData() {
        if (null != instructionAdapter)
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setInstructions(instructionAdapter.getAllSelectedString());
    }
}
