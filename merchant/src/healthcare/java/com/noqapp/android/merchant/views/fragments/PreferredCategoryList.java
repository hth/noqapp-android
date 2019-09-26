package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.noqapp.android.merchant.R;

public class PreferredCategoryList extends Fragment implements View.OnClickListener {

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
}
