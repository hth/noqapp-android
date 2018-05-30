package com.noqapp.android.client.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.noqapp.android.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserAdditionalInfoFragment extends Fragment {


    @BindView(R.id.edt_height)
    protected EditText edt_height;

    @BindView(R.id.edt_blood_group)
    protected EditText edt_blood_group;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_additional_info, container, false);
        ButterKnife.bind(this, view);
        updateUI();
        return view;
    }


    private void updateUI() {
        edt_blood_group.setText("B RhD positive (B+)");
        edt_height.setText("172 cm");
        edt_blood_group.setEnabled(false);
        edt_height.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

}
