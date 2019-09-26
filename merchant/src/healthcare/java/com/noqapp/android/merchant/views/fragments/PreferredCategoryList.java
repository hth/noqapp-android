package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ParentCheckBoxObj;

import java.util.List;

public class PreferredCategoryList extends Fragment implements View.OnClickListener {

    private CircleProgress dp_mri, dp_ctscan, dp_sono, dp_xray, dp_path, dp_special, dp_physio, dp_medicine;

    public void setCategoryListener(CategoryListener categoryListener) {
        this.categoryListener = categoryListener;
    }

    private CategoryListener categoryListener;

    public interface CategoryListener {
        void categorySelected(int id);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.frag_pref_store, container, false);
        CardView cv_mri = v.findViewById(R.id.cv_mri);
        CardView cv_ctscan = v.findViewById(R.id.cv_ctscan);
        CardView cv_sono = v.findViewById(R.id.cv_sono);
        CardView cv_xray = v.findViewById(R.id.cv_xray);
        CardView cv_path = v.findViewById(R.id.cv_path);
        CardView cv_special = v.findViewById(R.id.cv_special);
        CardView cv_physio = v.findViewById(R.id.cv_physio);
        CardView cv_medicine = v.findViewById(R.id.cv_medicine);

        dp_mri = v.findViewById(R.id.dp_mri);
        dp_ctscan = v.findViewById(R.id.dp_ctscan);
        dp_sono = v.findViewById(R.id.dp_sono);
        dp_xray = v.findViewById(R.id.dp_xray);
        dp_path = v.findViewById(R.id.dp_path);
        dp_special = v.findViewById(R.id.dp_special);
        dp_physio = v.findViewById(R.id.dp_physio);
        dp_medicine = v.findViewById(R.id.dp_medicine);

        cv_mri.setOnClickListener(this::onClick);
        cv_ctscan.setOnClickListener(this::onClick);
        cv_sono.setOnClickListener(this::onClick);
        cv_xray.setOnClickListener(this::onClick);
        cv_path.setOnClickListener(this::onClick);
        cv_special.setOnClickListener(this::onClick);
        cv_physio.setOnClickListener(this::onClick);
        cv_medicine.setOnClickListener(this::onClick);
        return v;
    }


    @Override
    public void onClick(View v) {
        if (null != categoryListener)
            categoryListener.categorySelected(v.getId());
    }

    public void updateProgress() {

        PreferredStoreFragment preferredStoreFragment = new PreferredStoreFragment();
        dp_mri.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.MRI)));
        dp_ctscan.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.SCAN)));
        dp_sono.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.SONO)));
        dp_xray.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.XRAY)));
        dp_path.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.PATH)));
        dp_special.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.SPEC)));
        dp_physio.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.PHYS)));
        dp_medicine.setProgress(checkCount(preferredStoreFragment.initCheckBoxList(null)));


        Log.e("MRI status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.MRI))));
        Log.e("CT Scan status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.SCAN))));
        Log.e("SONO status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.SONO))));
        Log.e("XRAY status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.XRAY))));

        Log.e("PATH status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.PATH))));
        Log.e("SPEC status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.SPEC))));
        Log.e("Physio status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(HealthCareServiceEnum.PHYS))));
        Log.e("Medicine status", String.valueOf(checkCount(preferredStoreFragment.initCheckBoxList(null))));

    }

    private int checkCount(List<ParentCheckBoxObj> temp) {
        if (null != temp && temp.size() > 0) {
            float k = 0;
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).getSelectedPos() != -1) {
                    k = k + 1;
                }
            }
            return (int)((k / temp.size()) * 100);
        } else {
            return 0;
        }
    }

}
