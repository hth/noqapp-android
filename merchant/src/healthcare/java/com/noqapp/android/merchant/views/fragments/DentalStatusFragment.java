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

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.DentalOptionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.adapters.ToothAdapter;
import com.noqapp.android.merchant.views.pojos.ToothInfo;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.List;

public class DentalStatusFragment extends DentalAnatomyFragment {
    private RecyclerView rcv_tooth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_dental_status, container, false);
        rcv_tooth = view.findViewById(R.id.rcv_tooth);
        rcv_tooth.setLayoutManager(new GridLayoutManager(getActivity(), 16));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToothProcedure imageFilePathTop = new ToothProcedure("top", DentalOptionEnum.NOR.getDescription());
        List<ToothProcedure> drawables = getFrontAllViews();
        List<ToothInfo> toothInfos = new ArrayList<>();
        List<String> toothNumbers = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getDentalDiagnosisList());
        for (int i = 0; i < 32; i++) {
            ToothInfo toothInfo = new ToothInfo();
            toothInfo.setToothNumber(Integer.parseInt(toothNumbers.get(i)));
            toothInfo.setToothFrontView(drawables.get(i));
            toothInfo.setToothDefaultFrontView(toothInfo.getToothFrontView());
            toothInfo.setToothTopView(imageFilePathTop);
            toothInfo.setToothDefaultTopView(toothInfo.getToothTopView());
            toothInfo.setFrontViewDrawables(getFrontOptionViews());
            toothInfo.setTopViewDrawables(getTopOptionViews());
            toothInfos.add(toothInfo);
        }
        ToothAdapter toothAdapter = new ToothAdapter(toothInfos, getActivity(), false);
        rcv_tooth.setAdapter(toothAdapter);
        try {
            JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getArguments().getSerializable("jsonMedicalRecord");
            if (null != jsonMedicalRecord && !TextUtils.isEmpty(
                    jsonMedicalRecord.getJsonUserMedicalProfile().getDentalAnatomy())) {
                String temp = jsonMedicalRecord.getJsonUserMedicalProfile().getDentalAnatomy();
                toothAdapter.updateToothObj(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}