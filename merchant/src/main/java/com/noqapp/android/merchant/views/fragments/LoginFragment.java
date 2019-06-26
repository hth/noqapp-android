package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.google.gson.Gson;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.LoginApiCalls;
import com.noqapp.android.merchant.model.MerchantProfileApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.LoginPresenter;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class LoginFragment extends BaseFragment implements LoginPresenter, MerchantPresenter {

    private Button btn_login;
    private EditText edt_pwd;
    private AutoCompleteTextView actv_email;
    private String email, pwd;
    private ArrayList<String> userList = new ArrayList<>();
    private LoginApiCalls loginApiCalls;
    private MerchantProfileApiCalls merchantProfileModel;
    private View view;

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        super.onCreateView(inflater, container, args);
        view = inflater.inflate(R.layout.frag_login, container, false);
        btn_login = view.findViewById(R.id.btn_login);
        actv_email = view.findViewById(R.id.actv_email);
        edt_pwd = view.findViewById(R.id.edt_pwd);
        userList = LaunchActivity.getLaunchActivity().getUserList();
        loginApiCalls = new LoginApiCalls(this);
        merchantProfileModel = new MerchantProfileApiCalls();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, userList);
        actv_email.setThreshold(1);//will start working from first character
        actv_email.setAdapter(adapter);
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AppUtils().hideKeyBoard(getActivity());
                if (isValidInput()) {
                 //   btn_login.setBackgroundResource(R.drawable.button_drawable_red);
                 //   btn_login.setTextColor(Color.WHITE);
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        showProgress();
                        setProgressMessage("Login in progress..");
                        loginApiCalls.login(email.toLowerCase(), pwd);

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
        LaunchActivity.getLaunchActivity().setActionBarTitle("");
        LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void loginResponse(String email, String auth) {
        if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(auth)) {
            LaunchActivity.getLaunchActivity().setUserInformation("", "", email, auth, true);
            setProgressMessage("Fetching your profile...");
            merchantProfileModel.setMerchantPresenter(this);
            merchantProfileModel.fetch(BaseLaunchActivity.getDeviceID(), email, auth);
            if (!userList.contains(email)) {
                userList.add(email);
                LaunchActivity.getLaunchActivity().setUserList(userList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, userList);
                //Getting the instance of AutoCompleteTextView
                actv_email.setThreshold(1);//will start working from first character
                actv_email.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
            }
        } else {
            dismissProgress();
            new CustomToast().showToast(getActivity(), getString(R.string.error_login));
            btn_login.setBackgroundResource(R.drawable.button_drawable);
            btn_login.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
        }
    }

    @Override
    public void loginError() {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        LaunchActivity.getLaunchActivity().clearLoginData(false);
        dismissProgress();
        if (null != eej) {
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.getCode())) {
                new CustomToast().showToast(getActivity(), getString(R.string.error_account_block));
                LaunchActivity.getLaunchActivity().clearLoginData(false);
            } else {
                new ErrorResponseHandler().processError(getActivity(), eej);
            }
        }
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        LaunchActivity.getLaunchActivity().clearLoginData(false);
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }


    @Override
    public void merchantResponse(JsonMerchant jsonMerchant) {
        if (null != jsonMerchant) {
            LaunchActivity.getLaunchActivity().setUserName(jsonMerchant.getJsonProfile().getName());
            LaunchActivity.getLaunchActivity().setUserLevel(jsonMerchant.getJsonProfile().getUserLevel().name());
            if (null != jsonMerchant.getJsonProfessionalProfile()) {
                PreferenceObjects map = new Gson().fromJson(jsonMerchant.getJsonProfessionalProfile().getDataDictionary(), PreferenceObjects.class);
                if (null != map) {
                    LaunchActivity.getLaunchActivity().setSuggestionsPrefs(map);
                }
            }
            if (jsonMerchant.getJsonProfile().getUserLevel() == UserLevelEnum.Q_SUPERVISOR || jsonMerchant.getJsonProfile().getUserLevel() == UserLevelEnum.S_MANAGER) {
                if ((getActivity().getPackageName().equalsIgnoreCase("com.noqapp.android.merchant.healthcare") &&
                        jsonMerchant.getJsonProfile().getBusinessType() == BusinessTypeEnum.DO) ||
                        (getActivity().getPackageName().equalsIgnoreCase("com.noqapp.android.merchant") &&
                                jsonMerchant.getJsonProfile().getBusinessType() != BusinessTypeEnum.DO)||getActivity().getPackageName().equalsIgnoreCase("com.noqapp.android.merchant.tv")) {
                    if (new AppUtils().isTablet(getActivity())) {
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.6f);
                        view.setVisibility(View.GONE); // to hide  the login view when screen resize
                        LaunchActivity.getLaunchActivity().list_fragment.setLayoutParams(lp1);
                        LaunchActivity.getLaunchActivity().list_detail_fragment.setLayoutParams(lp2);
                        LaunchActivity.getLaunchActivity().enableDisableDrawer(true);
                    }
                    LaunchActivity.getLaunchActivity().setAccessGrant(true);
                    LaunchActivity.getLaunchActivity().setUserProfile(jsonMerchant.getJsonProfile());
                    LaunchActivity.getLaunchActivity().setUserProfessionalProfile(jsonMerchant.getJsonProfessionalProfile());
                    LaunchActivity.getLaunchActivity().updateMenuList(jsonMerchant.getJsonProfile().getUserLevel() == UserLevelEnum.S_MANAGER);
                    MerchantListFragment mlf = new MerchantListFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("jsonMerchant", jsonMerchant);
                    mlf.setArguments(b);
                    LaunchActivity.setMerchantListFragment(mlf);
                    LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, mlf);
                } else {
                    // unauthorised to see the screen
                    LaunchActivity.getLaunchActivity().setAccessGrant(false);
                    AccessDeniedFragment adf = new AccessDeniedFragment();
                    Bundle b = new Bundle();
                    b.putString("errorMsg", "You are login in the wrong app please login in the correct app");
                    adf.setArguments(b);
                    LaunchActivity.getLaunchActivity().clearLoginData(false);
                    LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, adf);
                }
            } else {
                // unauthorised to see the screen
                LaunchActivity.getLaunchActivity().setAccessGrant(false);
                AccessDeniedFragment adf = new AccessDeniedFragment();
                Bundle b = new Bundle();
                b.putString("errorMsg", getString(R.string.error_access_denied));
                adf.setArguments(b);
                LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, adf);
            }
            LaunchActivity.getLaunchActivity().setUserName();
        }
        dismissProgress();
    }

    @Override
    public void merchantError() {
        dismissProgress();
        LaunchActivity.getLaunchActivity().clearLoginData(false);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
       // AppUtils.authenticationProcessing();
        //On Login screen need not to clear the data
        ShowAlertInformation.showThemeDialog(getActivity(),"Invalid Credentail","There was an error with your E-Mail/Password combination. Please try again.");
    }

    private boolean isValidInput() {
        boolean isValid = true;
        email = actv_email.getText().toString().trim();
        pwd = edt_pwd.getText().toString().trim();
        actv_email.setError(null);
        edt_pwd.setError(null);
       // btn_login.setBackgroundResource(R.drawable.button_drawable);
       // btn_login.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
        if (TextUtils.isEmpty(email)) {
            actv_email.setError(getString(R.string.error_email_blank));
            isValid = false;
        }
        if (!TextUtils.isEmpty(email) && !new CommonHelper().isValidEmail(email)) {
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
