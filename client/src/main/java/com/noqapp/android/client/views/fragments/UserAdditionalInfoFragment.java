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
import com.noqapp.library.beans.JsonNameDatePair;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserAdditionalInfoFragment extends Fragment {


    @BindView(R.id.tv_education)
    protected TextView tv_education;

    @BindView(R.id.tv_experience)
    protected TextView tv_experience;
    private List<JsonNameDatePair> education = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_additional_info, container, false);
        ButterKnife.bind(this, view);
        education.add(new JsonNameDatePair().setName("BHMS").setMonthYear("2006"));
        education.add(new JsonNameDatePair().setName("Solan Homeopathic Medical College & Hospital").setMonthYear("2008"));
        education.add(new JsonNameDatePair().setName("MBA (Healthcare)").setMonthYear("2010"));
        education.add(new JsonNameDatePair().setName("National Institute of Business Management").setMonthYear("2016"));

        updateUI();
        return view;
    }


    private void updateUI() {

        StringBuilder text_edu = new StringBuilder();
        for (int i =0 ; i< education.size();i++){
            text_edu.append( education.get(i).getName()) ;
            text_edu.append( TextUtils.isEmpty(education.get(i).getMonthYear())? "": ", "+education.get(i).getMonthYear()+"\n");
        }
        tv_experience.setText(text_edu);
        tv_education.setText("172 cm");

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

}
