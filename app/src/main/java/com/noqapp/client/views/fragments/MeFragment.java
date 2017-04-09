package com.noqapp.client.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.BitmapCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.client.R;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends NoQueueBaseFragment {


    public static final String TAG = "MeFragment";
    public MeFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance() {
        return new MeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Me");

    }

    @OnClick(R.id.btnRegistration)
    public void action_registration(View view)
    {
        replaceFragmentWithBackStack(getActivity(),R.id.frame_layout,new RegistrationFormFragment(),TAG);
    }

    @OnClick(R.id.btnLogin)
    public void action_Login(View view)
    {

    }
}
