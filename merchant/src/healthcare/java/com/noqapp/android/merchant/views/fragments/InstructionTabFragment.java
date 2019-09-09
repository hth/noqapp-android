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

public class InstructionTabFragment extends BaseFragment {
    private InstructionFragment instructionFragment;
    private DentalWorkDoneFragment dentalWorkDoneFragment;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view =  inflater.inflate(R.layout.frag_instruction_tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            FrameLayout fl_instruction = view.findViewById(R.id.fl_instruction);
            FrameLayout fl_dental_work_done = view.findViewById(R.id.fl_dental_work_done);
            if (MedicalDepartmentEnum.valueOf(MedicalCaseActivity.getMedicalCaseActivity().bizCategoryId) == MedicalDepartmentEnum.DNT && LaunchActivity.isTablet) {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.6f);
                fl_instruction.setLayoutParams(lp1);
                fl_dental_work_done.setLayoutParams(lp2);
            }

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
