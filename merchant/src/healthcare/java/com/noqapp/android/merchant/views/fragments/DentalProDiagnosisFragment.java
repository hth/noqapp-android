package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.model.types.medical.DentalOptionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.ToothAdapter;
import com.noqapp.android.merchant.views.pojos.ToothInfo;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.List;

public class DentalProDiagnosisFragment extends BaseFragment {
    private ToothAdapter toothAdapter;
    private RecyclerView rcv_tooth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_dental, container, false);
        rcv_tooth = view.findViewById(R.id.rcv_tooth);
        rcv_tooth.setLayoutManager(new GridLayoutManager(getActivity(), 16));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToothProcedure imageFilePathTop = new ToothProcedure(R.drawable.top, DentalOptionEnum.NOR.getDescription());
        List<ToothProcedure> drawables = getFrontAllViews();
        List<ToothInfo> toothInfos = new ArrayList<>();
        List<String> toothNumbers = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getDentalDiagnosisList());
        for (int i = 0; i < 32; i++) {
            ToothInfo toothInfo = new ToothInfo();
            toothInfo.setToothNumber(Integer.parseInt(toothNumbers.get(i)));
            //toothInfo.setToothFrontView(drawables.get(i));
            toothInfo.setToothFrontView(getTempDrawable(i + 1));
            toothInfo.setToothDefaultFrontView(toothInfo.getToothFrontView());
            toothInfo.setToothTopView(imageFilePathTop);
            toothInfo.setToothDefaultTopView(toothInfo.getToothTopView());
            toothInfo.setFrontViewDrawables(getFrontOptionViews());
            toothInfo.setTopViewDrawables(getTopOptionViews());
            toothInfos.add(toothInfo);
        }
        toothAdapter = new ToothAdapter(toothInfos, getActivity());
        rcv_tooth.setAdapter(toothAdapter);
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord() && !TextUtils.isEmpty(
                    MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getJsonUserMedicalProfile().getDentalAnatomy())) {
                String temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getJsonUserMedicalProfile().getDentalAnatomy();
                toothAdapter.updateToothObj(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<ToothProcedure> getTopOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        List<String> options = DentalOptionEnum.asListOfDescription();
        int[] drawable = new int[6];
        for (int i = 0; i < 6; i++) {
            drawable[i] = this.getResources().getIdentifier(String.valueOf("tooth_o_2_" + (i + 1)), "drawable", getActivity().getPackageName());
        }
        for (int i = 0; i < options.size(); i++) {
            int pos = i % 6;
            drawables.add(new ToothProcedure(drawable[pos], options.get(i)));
        }
        return drawables;
    }

    private List<ToothProcedure> getFrontOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        List<String> options = DentalOptionEnum.asListOfDescription();
        int[] drawable = new int[4];
        for (int i = 0; i < 4; i++) {
            drawable[i] = this.getResources().getIdentifier(String.valueOf("tooth_o_1_" + (i + 1)), "drawable", getActivity().getPackageName());
        }
        for (int i = 0; i < options.size(); i++) {
            int pos = i % 4;
            drawables.add(new ToothProcedure(drawable[pos], options.get(i)));
        }
        return drawables;
    }

    private List<ToothProcedure> getFrontAllViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_1_" + (i + 1)), "drawable", getActivity().getPackageName());
            drawables.add(new ToothProcedure(id, DentalOptionEnum.NOR.getDescription()));
        }
        return drawables;
    }

    public void saveData() {
        // save to dental anatomy
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDentalAnatomy(toothAdapter.getSelectedData());
        if (!TextUtils.isEmpty(toothAdapter.getSelectedData())) {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDentalAnatomyFilled(true);
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDentalAnatomyFilled(false);
        }
    }


    private ToothProcedure getTempDrawable(int pos) {
        if (pos == 1 || pos == 2 || pos == 3 || pos == 16 || pos == 15 || pos == 14) {
            return new ToothProcedure(R.drawable.t_1, DentalOptionEnum.NOR.getDescription());
        } else if (pos == 4 || pos == 13) {
            return new ToothProcedure(R.drawable.t_2, DentalOptionEnum.NOR.getDescription());
        } else if (pos > 4 && pos < 13) {
            return new ToothProcedure(R.drawable.t_3, DentalOptionEnum.NOR.getDescription());
        } else if (pos == 17 || pos == 18 || pos == 19 || pos == 32 || pos == 31 || pos == 30) {
            return new ToothProcedure(R.drawable.t_4, DentalOptionEnum.NOR.getDescription());
        } else if (pos == 20 || pos == 29) {
            return new ToothProcedure(R.drawable.t_5, DentalOptionEnum.NOR.getDescription());
        } else if (pos > 20 && pos < 29) {
            return new ToothProcedure(R.drawable.t_6, DentalOptionEnum.NOR.getDescription());
        } else {
            return new ToothProcedure(R.drawable.t_3, DentalOptionEnum.NOR.getDescription());
        }
    }

}