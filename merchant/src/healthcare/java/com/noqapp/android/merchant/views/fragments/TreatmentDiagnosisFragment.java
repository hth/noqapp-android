package com.noqapp.android.merchant.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;

public class TreatmentDiagnosisFragment extends BaseFragment implements
        AutoCompleteAdapterNew.SearchClick, AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView recyclerView_one;
    private TextView tv_add_diagnosis;
    private StaggeredGridAdapter diagnosisAdapter;
    private AutoCompleteTextView actv_search_dia;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_treatment_diagnosis, container, false);
        recyclerView_one = v.findViewById(R.id.recyclerViewOne);
        tv_add_diagnosis = v.findViewById(R.id.tv_add_diagnosis);
        tv_add_diagnosis.setOnClickListener(v12 -> AddItemDialog(getActivity(), "Add Diagnosis"));
        actv_search_dia = v.findViewById(R.id.actv_search_dia);
        actv_search_dia.setThreshold(1);
        ImageView iv_clear_actv_dia = v.findViewById(R.id.iv_clear_actv_dia);
        iv_clear_actv_dia.setOnClickListener(v15 -> {
            actv_search_dia.setText("");
            new AppUtils().hideKeyBoard(getActivity());
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView_one.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        diagnosisAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList());
        recyclerView_one.setAdapter(diagnosisAdapter);
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getDiagnosis()) {
                String[] temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getDiagnosis().split(",");
                diagnosisAdapter.updateSelection(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AutoCompleteAdapterNew adapterSearchDia = new AutoCompleteAdapterNew(getActivity(), R.layout.layout_autocomplete, MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList(), null, this);
        actv_search_dia.setAdapter(adapterSearchDia);
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
                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList();
                temp.add(new DataObj(edt_item.getText().toString(), false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setDiagnosisList(temp);
                recyclerView_one.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList());
                recyclerView_one.setAdapter(customAdapter);
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getDiagnosisList().add(new DataObj(edt_item.getText().toString(), false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                new CustomToast().showToast(getActivity(), "'" + edt_item.getText().toString() + "' added successfully to list");
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }


    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDiagnosis(diagnosisAdapter.getSelectedData());
    }


    @Override
    public void searchClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos) {
        new AppUtils().hideKeyBoard(getActivity());
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        new AppUtils().hideKeyBoard(getActivity());
        actv_search_dia.setText("");
        diagnosisAdapter.selectItem(dataObj);
        diagnosisAdapter.notifyDataSetChanged();
    }
}
