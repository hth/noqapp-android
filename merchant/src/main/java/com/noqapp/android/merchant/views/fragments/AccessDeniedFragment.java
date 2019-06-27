package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

public class AccessDeniedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_access_denied, container, false);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(getArguments().getString("errorMsg",getString(R.string.error_access_denied)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.app_name));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.GONE);
        if (new AppUtils().isTablet(getActivity())) {
            LaunchActivity.getLaunchActivity().enableDisableBack(false);
        } else {
            LaunchActivity.getLaunchActivity().enableDisableBack(true);
        }
    }
}
