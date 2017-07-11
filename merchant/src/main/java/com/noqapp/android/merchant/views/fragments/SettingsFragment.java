package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.LoginModel;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.LoginPresenter;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;

import org.apache.commons.lang3.StringUtils;

public class SettingsFragment extends Fragment implements LoginPresenter, MerchantPresenter {

    private TextView tv_title;
    private ToggleButton toggleDayClosed,togglePreventJoin;

    public SettingsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.frag_settings, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Bundle b = getArguments();
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        toggleDayClosed = (ToggleButton) view.findViewById(R.id.toggleDayClosed);
        togglePreventJoin = (ToggleButton) view.findViewById(R.id.togglePreventJoin);
        if(null != b){
            tv_title.setText(b.getString("title",""));
        }
        LoginModel.loginPresenter = this;
        MerchantProfileModel.merchantPresenter = this;

        toggleDayClosed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
               // NoQueueBaseActivity.setAutoJoinStatus(isChecked);
            }
        });

        togglePreventJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
              //  NoQueueBaseActivity.setAutoJoinStatus(isChecked);
            }
        });
//        btn_login.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                    if (LaunchActivity.getLaunchActivity().isOnline()) {
//                        LaunchActivity.getLaunchActivity().progressDialog.show();
//                        LoginModel.login(email, pwd);
//                    } else {
//                        ShowAlertInformation.showNetworkDialog(getActivity());
//                    }
//
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_settings));
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.VISIBLE);
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
        LaunchActivity.getLaunchActivity().enableLogout();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void loginResponse(String email, String auth) {
        if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(auth)) {
            LaunchActivity.getLaunchActivity().setSharedPreferance("", "", email, auth, true);
            MerchantProfileModel.fetch(email, auth);
        } else {
            LaunchActivity.getLaunchActivity().dismissProgress();
            Toast.makeText(getActivity(), getString(R.string.error_login), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void loginError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void merchantResponse(JsonMerchant jsonMerchant) {
        if (null != jsonMerchant) {
            LaunchActivity.getLaunchActivity().setUserName(jsonMerchant.getJsonProfile().getName());
            LaunchActivity.getLaunchActivity().setUserLevel(jsonMerchant.getJsonProfile().getUserLevel().name());
            MerchantListFragment mlf = new MerchantListFragment();
            Bundle b = new Bundle();
            b.putSerializable("jsonMerchant", jsonMerchant);
            mlf.setArguments(b);
            if(new AppUtils().isTablet(getActivity())){
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.3f);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.6f);
                LaunchActivity.getLaunchActivity().list_fragment.setLayoutParams(lp1);
                LaunchActivity.getLaunchActivity().list_detail_fragment.setLayoutParams(lp2);
            }
            LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, mlf);
            LaunchActivity.getLaunchActivity().setUserName();
        }
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void merchantError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


}
