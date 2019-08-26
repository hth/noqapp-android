package com.noqapp.android.merchant.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;

public class InstructionTabFragment extends BaseFragment {
    private InstructionFragment instructionFragment;
    private DentalWorkDoneFragment dentalWorkDoneFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.frag_instruction_tab, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            instructionFragment = new InstructionFragment();
            getFragmentTransaction().replace(R.id.fl_instruction, instructionFragment).commit();
            if (MedicalDepartmentEnum.valueOf(MedicalCaseActivity.getMedicalCaseActivity().bizCategoryId) == MedicalDepartmentEnum.DNT) {
                dentalWorkDoneFragment = new DentalWorkDoneFragment();
                getFragmentTransaction().replace(R.id.fl_dental_work_done, dentalWorkDoneFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        if (null != dentalWorkDoneFragment)
            dentalWorkDoneFragment.saveData();

        instructionFragment.saveData();
    }

}
