package com.noqapp.android.client.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.library.beans.JsonHealthCareProfile;
import com.noqapp.library.beans.JsonNameDatePair;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserAdditionalInfoFragment extends Fragment {


    @BindView(R.id.tv_education)
    protected TextView tv_education;

    @BindView(R.id.tv_experience)
    protected TextView tv_experience;

    private JsonHealthCareProfile jsonHealthCareProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_additional_info, container, false);
        ButterKnife.bind(this, view);
        if (null != jsonHealthCareProfile)
            updateUI(jsonHealthCareProfile);
        return view;
    }


    public void updateUI(JsonHealthCareProfile jsonHealthCareProfile) {
        this.jsonHealthCareProfile = jsonHealthCareProfile;
        List<JsonNameDatePair> experience = jsonHealthCareProfile.getAwards();
        List<JsonNameDatePair> education = jsonHealthCareProfile.getEducation();

        StringBuilder text_edu = new StringBuilder();
        for (int i = 0; i < education.size(); i++) {
            text_edu.append(education.get(i).getName());
            text_edu.append(TextUtils.isEmpty(education.get(i).getMonthYear()) ? "" : ", " + AppUtilities.getYearFromDate(education.get(i).getMonthYear()) + "\n");
        }

        StringBuilder text_exp = new StringBuilder();
        for (int i = 0; i < experience.size(); i++) {
            text_exp.append(experience.get(i).getName());
            text_exp.append(TextUtils.isEmpty(experience.get(i).getMonthYear()) ? "" : ", " + AppUtilities.getYearFromDate(experience.get(i).getMonthYear()) + "\n");
        }
        tv_experience.setText(text_exp);
        tv_education.setText(text_edu);
    }

}
