package com.noqapp.android.merchant.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */


import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;


public class UserAdditionalInfoFragment extends Fragment {


    @BindView(R.id.tv_education)
    protected TextView tv_education;

    @BindView(R.id.tv_experience)
    protected TextView tv_experience;
//
    private JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_additional_info, container, false);
        ButterKnife.bind(this, view);
        if (null != jsonProfessionalProfilePersonal)
            updateUI(jsonProfessionalProfilePersonal);
        return view;
    }


    public void updateUI(JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        this.jsonProfessionalProfilePersonal = jsonProfessionalProfilePersonal;
        List<JsonNameDatePair> experience = jsonProfessionalProfilePersonal.getAwards();
        List<JsonNameDatePair> education = jsonProfessionalProfilePersonal.getEducation();

        StringBuilder text_edu = new StringBuilder();
        for (int i = 0; i < education.size(); i++) {
            text_edu.append(TextUtils.isEmpty(education.get(i).getMonthYear()) ? "" : AppUtils.getYearFromDate(education.get(i).getMonthYear()) + ", " );
            text_edu.append(education.get(i).getName()+ "\n");
        }

        StringBuilder text_exp = new StringBuilder();
        for (int i = 0; i < experience.size(); i++) {
            text_exp.append(TextUtils.isEmpty(experience.get(i).getMonthYear()) ? "" : AppUtils.getYearFromDate(experience.get(i).getMonthYear()) + ", " );
            text_exp.append(experience.get(i).getName()+ "\n");
        }
        tv_experience.setText(text_exp);
        tv_education.setText(text_edu);
    }

}
