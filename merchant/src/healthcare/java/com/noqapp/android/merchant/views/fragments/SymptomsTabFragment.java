package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.noqapp.android.merchant.R;

public class SymptomsTabFragment extends BaseFragment {
    private PastHistoryFragment pastHistoryFragment;
    private SymptomsFragment symptomsFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.frag_symptoms_tab, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            pastHistoryFragment = new PastHistoryFragment();
            getFragmentTransaction().replace(R.id.fl_past_history, pastHistoryFragment).commit();
            symptomsFragment = new SymptomsFragment();
            getFragmentTransaction().replace(R.id.fl_symptoms, symptomsFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        pastHistoryFragment.saveData();
        symptomsFragment.saveData();
    }
}
