package com.noqapp.android.merchant.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.LoginModel;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.model.types.UserLevelEnum;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.LoginPresenter;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class LoginFragment extends Fragment implements LoginPresenter, MerchantPresenter {

    private Button btn_login;
    private EditText  edt_pwd;
    private AutoCompleteTextView actv_email;
    private String email, pwd;

    private ArrayList<String> userList = new ArrayList<>();


    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.frag_login, container, false);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        actv_email = (AutoCompleteTextView) view.findViewById(R.id.actv_email);
        edt_pwd = (EditText) view.findViewById(R.id.edt_pwd);
        userList = LaunchActivity.getLaunchActivity().getUserList();
        LoginModel.loginPresenter = this;
        MerchantProfileModel.merchantPresenter = this;
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, userList);
        //Getting the instance of AutoCompleteTextView

        actv_email.setThreshold(1);//will start working from first character
        actv_email.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyBoard();
                if (isValidInput()) {
                    btn_login.setBackgroundResource(R.drawable.button_drawable_red);
                    btn_login.setTextColor(Color.WHITE);
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        LoginModel.login(email.toLowerCase(), pwd);

                        Answers.getInstance().logLogin(new LoginEvent()
                                .putMethod("Email_Password_Login")
                                .putSuccess(true));
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
            LaunchActivity.getLaunchActivity().setUserInformation("", "", email, auth, true);
            MerchantProfileModel.fetch(email, auth);
            if(!userList.contains(email)){
                userList.add(email);
                LaunchActivity.getLaunchActivity().setUserList(userList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (getActivity(), android.R.layout.select_dialog_item, userList);
                //Getting the instance of AutoCompleteTextView

                actv_email.setThreshold(1);//will start working from first character
                actv_email.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
            }
        } else {
            LaunchActivity.getLaunchActivity().dismissProgress();
            Toast.makeText(getActivity(), getString(R.string.error_login), Toast.LENGTH_LONG).show();
            btn_login.setBackgroundResource(R.drawable.button_drawable);
            btn_login.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
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


            if(jsonMerchant.getJsonProfile().getUserLevel() == UserLevelEnum.Q_SUPERVISOR || jsonMerchant.getJsonProfile().getUserLevel()== UserLevelEnum.S_MANAGER ) {
                if (new AppUtils().isTablet(getActivity())) {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.3f);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.6f);
                    LaunchActivity.getLaunchActivity().list_fragment.setLayoutParams(lp1);
                    LaunchActivity.getLaunchActivity().list_detail_fragment.setLayoutParams(lp2);
                }
                LaunchActivity.getLaunchActivity().setAccessGrant(true);
                MerchantListFragment mlf = new MerchantListFragment();
                Bundle b = new Bundle();
                b.putSerializable("jsonMerchant", jsonMerchant);
                mlf.setArguments(b);
                LaunchActivity.setMerchantListFragment(mlf);
                LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, mlf);
            }else{
                // unauthorised to see the screen
                LaunchActivity.getLaunchActivity().setAccessGrant(false);
                AccessDeniedFragment adf = new AccessDeniedFragment();
                LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, adf);
            }
            LaunchActivity.getLaunchActivity().setUserName();
        }
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void merchantError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            LaunchActivity.getLaunchActivity().clearLoginData(true);
        }
    }

    private void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isValidInput() {
        boolean isValid = true;
        email = actv_email.getText().toString().trim();
        pwd = edt_pwd.getText().toString().trim();
        actv_email.setError(null);
        edt_pwd.setError(null);
        btn_login.setBackgroundResource(R.drawable.button_drawable);
        btn_login.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
        if (TextUtils.isEmpty(email)) {
            actv_email.setError(getString(R.string.error_email_blank));
            isValid = false;
        }
        if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
            actv_email.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }
        if (pwd.equals("")) {
            edt_pwd.setError(getString(R.string.error_pwd_blank));
            isValid = false;
        }
        if (!pwd.equals("") && pwd.length() < 6) {
            edt_pwd.setError(getString(R.string.error_pwd_length));
            isValid = false;
        }
        return isValid;
    }
}
