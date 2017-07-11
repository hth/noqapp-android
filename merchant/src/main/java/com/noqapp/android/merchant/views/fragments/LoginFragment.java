package com.noqapp.android.merchant.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.LoginModel;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.LoginPresenter;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;

import org.apache.commons.lang3.StringUtils;

public class LoginFragment extends Fragment implements LoginPresenter, MerchantPresenter {

    private Button btn_login;
    private EditText edt_email, edt_pwd;

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.frag_login, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btn_login = (Button) view.findViewById(R.id.btn_login);
        edt_email = (EditText) view.findViewById(R.id.edt_email);
        edt_pwd = (EditText) view.findViewById(R.id.edt_pwd);
        LoginModel.loginPresenter = this;
        MerchantProfileModel.merchantPresenter = this;
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyBoard();
                String email = edt_email.getText().toString().trim();
                String pwd = edt_pwd.getText().toString().trim();
                edt_email.setError(null);
                edt_pwd.setError(null);
                if (TextUtils.isEmpty(email)) {
                    edt_email.setError(getString(R.string.error_email_blank));
                }
                if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
                    edt_email.setError(getString(R.string.error_email_invalid));
                } else if (pwd.equals("")) {
                    edt_pwd.setError(getString(R.string.error_pwd_blank));
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        LoginModel.login(email, pwd);
                    } else {
                        ShowAlertInformation.showNetworkDialog(getActivity());
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity();
        LaunchActivity.getLaunchActivity().setActionBarTitle("");
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.GONE);
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

    private void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
