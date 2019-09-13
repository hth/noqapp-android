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

public class ProDiagnosisFragment extends BaseFragment implements AutoCompleteAdapterNew.SearchByPos {

    private RecyclerView rcv_provisional_diagnosis;
    private StaggeredGridAdapter provisionalDiagnosisAdapter;
    private AutoCompleteTextView actv_search_provisional_dia;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_pro_diagnosis, container, false);
        TextView tv_add_provisional_diagnosis = v.findViewById(R.id.tv_add_provisional_diagnosis);
        rcv_provisional_diagnosis = v.findViewById(R.id.rcv_provisional_diagnosis);
        tv_add_provisional_diagnosis.setOnClickListener(v1 -> AddItemDialog(getActivity()));
        actv_search_provisional_dia = v.findViewById(R.id.actv_search_provisional_dia);
        actv_search_provisional_dia.setThreshold(1);
        ImageView iv_clear_actv_dia = v.findViewById(R.id.iv_clear_actv_dia);
        iv_clear_actv_dia.setOnClickListener(v12 -> {
            actv_search_provisional_dia.setText("");
            AppUtils.hideKeyBoard(getActivity());
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rcv_provisional_diagnosis.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));
        provisionalDiagnosisAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList());
        rcv_provisional_diagnosis.setAdapter(provisionalDiagnosisAdapter);
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord() && null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getProvisionalDifferentialDiagnosis()) {
                String[] temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getProvisionalDifferentialDiagnosis().split(",");
                provisionalDiagnosisAdapter.updateSelection(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AutoCompleteAdapterNew adapterSearchPath = new AutoCompleteAdapterNew(getActivity(), R.layout.layout_autocomplete, MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList(), null, this);
        actv_search_provisional_dia.setAdapter(adapterSearchPath);
    }

    private void AddItemDialog(Context mContext) {
        ShowAddDialog showDialog = new ShowAddDialog(mContext);
        showDialog.setDialogClickListener(new ShowAddDialog.DialogClickListener() {
            @Override
            public void btnDoneClick(String str) {
                ArrayList<DataObj> temp = MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList();
                temp.add(new DataObj(str, false).setNewlyAdded(true));
                MedicalCaseActivity.getMedicalCaseActivity().formDataObj.setProvisionalDiagnosisList(temp);
                rcv_provisional_diagnosis.setLayoutManager(MedicalCaseActivity.getMedicalCaseActivity().getFlexBoxLayoutManager(getActivity()));

                provisionalDiagnosisAdapter = new StaggeredGridAdapter(getActivity(), MedicalCaseActivity.getMedicalCaseActivity().formDataObj.getProvisionalDiagnosisList());
                rcv_provisional_diagnosis.setAdapter(provisionalDiagnosisAdapter);
                MedicalCaseActivity.getMedicalCaseActivity().getPreferenceObjects().getProDiagnosisList().add(new DataObj(str, false));
                MedicalCaseActivity.getMedicalCaseActivity().updateSuggestions();
                new CustomToast().showToast(getActivity(), "'" + str + "' added successfully to list");
            }
        });
        showDialog.displayDialog("Add Provisional Diagnosis");
    }

    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setProvisionalDiagnosis(provisionalDiagnosisAdapter.getSelectedData());
    }

    @Override
    public void searchByPos(DataObj dataObj) {
        AppUtils.hideKeyBoard(getActivity());
        actv_search_provisional_dia.setText("");
        provisionalDiagnosisAdapter.selectItem(dataObj);
        provisionalDiagnosisAdapter.notifyDataSetChanged();

    }
}
