package com.noqapp.android.merchant.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.AutoCompleteAdapterNew;
import com.noqapp.android.merchant.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.utils.ShowAddDialog;

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
        tv_add_diagnosis.setOnClickListener(v12 -> AddItemDialog(getActivity()));
        actv_search_dia = v.findViewById(R.id.actv_search_dia);
        actv_search_dia.setThreshold(1);
        ImageView iv_clear_actv_dia = v.findViewById(R.id.iv_clear_actv_dia);
        iv_clear_actv_dia.setOnClickListener(v15 -> {
            actv_search_dia.setText("");
            AppUtils.hideKeyBoard(getActivity());
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

    private void AddItemDialog(Context mContext) {
        ShowAddDialog showDialog = new ShowAddDialog(mContext);
        showDialog.setDialogClickListener(new ShowAddDialog.DialogClickListener() {
            @Override
            public void btnDoneClick(String str) {
                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList();
                temp.add(new DataObj(str, false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setDiagnosisList(temp);
                recyclerView_one.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
                StaggeredGridAdapter customAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getDiagnosisList());
                recyclerView_one.setAdapter(customAdapter);
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getDiagnosisList().add(new DataObj(str, false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                new CustomToast().showToast(getActivity(), "'" + str + "' added successfully to list");
            }
        });
        showDialog.displayDialog("Add Diagnosis");
    }


    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDiagnosis(diagnosisAdapter.getSelectedData());
    }


    @Override
    public void searchClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos) {
        AppUtils.hideKeyBoard(getActivity());
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        AppUtils.hideKeyBoard(getActivity());
        actv_search_dia.setText("");
        diagnosisAdapter.selectItem(dataObj);
        diagnosisAdapter.notifyDataSetChanged();
    }
}
