package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;

public class TreatmentTabFragment extends BaseFragment {
    private TreatmentDiagnosisFragment treatmentDiagnosisFragment;
    private TreatmentMedicineFragment treatmentMedicineFragment;
    private TreatmentDiagnosisDentalFragment treatmentDiagnosisDentalFragment;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.frag_treatment_tab, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            FrameLayout fl_treatment_diagnosis = view.findViewById(R.id.fl_treatment_diagnosis);
            FrameLayout fl_treatment_medicine = view.findViewById(R.id.fl_treatment_medicine);
            if (MedicalDepartmentEnum.valueOf(MedicalCaseActivity.getMedicalCaseActivity().bizCategoryId) == MedicalDepartmentEnum.DNT && LaunchActivity.isTablet) {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.7f);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
                fl_treatment_diagnosis.setLayoutParams(lp1);
                fl_treatment_medicine.setLayoutParams(lp2);
            }
            treatmentMedicineFragment = new TreatmentMedicineFragment();
            getFragmentTransaction().replace(R.id.fl_treatment_medicine, treatmentMedicineFragment).commit();
            if (MedicalDepartmentEnum.valueOf(MedicalCaseActivity.getMedicalCaseActivity().bizCategoryId) == MedicalDepartmentEnum.DNT) {
                treatmentDiagnosisDentalFragment = new TreatmentDiagnosisDentalFragment();
                getFragmentTransaction().replace(R.id.fl_treatment_diagnosis, treatmentDiagnosisDentalFragment).commit();
            } else {
                treatmentDiagnosisFragment = new TreatmentDiagnosisFragment();
                getFragmentTransaction().replace(R.id.fl_treatment_diagnosis, treatmentDiagnosisFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        if (null != treatmentDiagnosisFragment) {
            treatmentDiagnosisFragment.saveData();
        }
        if (null != treatmentDiagnosisDentalFragment) {
            treatmentDiagnosisDentalFragment.saveData();
        }
        treatmentMedicineFragment.saveData();
    }
}
