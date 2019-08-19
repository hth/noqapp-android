package com.noqapp.android.merchant.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.noqapp.android.merchant.R;

public class TreatmentTabFragment extends BaseFragment {
    private TreatmentDiagnosisFragment treatmentDiagnosisFragment;
    private TreatmentMedicineFragment treatmentMedicineFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_treatment_tab, container, false);
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            treatmentDiagnosisFragment = new TreatmentDiagnosisFragment();
            treatmentMedicineFragment = new TreatmentMedicineFragment();
            transaction.replace(R.id.fl_treatment_diagnosis, treatmentDiagnosisFragment).commit();
            transaction.replace(R.id.fl_treatment_medicine, treatmentMedicineFragment).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
        return v;
    }


    public void saveData() {
        treatmentDiagnosisFragment.saveData();
        treatmentMedicineFragment.saveData();
    }

}
