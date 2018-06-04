package com.noqapp.android.client.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noqapp.android.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserProfileFragment extends Fragment {




    @BindView(R.id.tv_available_other_place)
    protected TextView tv_available_other_place;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);

        updateUI();

        return view;
    }

    private void updateUI() {
        //tv_available_other_place.setText("");


    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

}
