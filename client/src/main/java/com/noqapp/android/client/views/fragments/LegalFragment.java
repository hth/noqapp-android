package com.noqapp.android.client.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.WebViewActivity;

public class LegalFragment extends NoQueueBaseFragment implements View.OnClickListener {

    private LinearLayout ll_term_condition, ll_privacy_policy, ll_about_us;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_legel, container, false);
        TextView tv_version = (TextView) view.findViewById(R.id.tv_version);
        tv_version.setText(BuildConfig.BUILD_TYPE.equalsIgnoreCase("release") ? getString(R.string.version_no, BuildConfig.VERSION_NAME)
                : getString(R.string.version_no, "Not for release"));
        ll_term_condition = (LinearLayout) view.findViewById(R.id.ll_term_condition);
        ll_privacy_policy = (LinearLayout) view.findViewById(R.id.ll_privacy_policy);
        ll_about_us = (LinearLayout) view.findViewById(R.id.ll_about_us);
        ll_term_condition.setOnClickListener(this);
        ll_privacy_policy.setOnClickListener(this);
        ll_about_us.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_legal));
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String url = "";
        switch (id) {
            case R.id.ll_term_condition:
                url = Constants.URL_TERM_CONDITION;
                break;
            case R.id.ll_privacy_policy:
                url = Constants.URL_PRIVACY_POLICY;
                break;
            case R.id.ll_about_us:
                url = Constants.URL_ABOUT_US;
                break;
            default:
                break;
        }

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            Intent in = new Intent(getActivity(), WebViewActivity.class);
            in.putExtra("url", url);
            getActivity().startActivity(in);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }
}
