package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.RegisterApiCall;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.version_2.HomeActivity;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;

public class LoginActivity extends OTPActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        activity = this;
        tv_toolbar_title.setText("Login");
    }

    @Override
    protected void callApi(String phoneNumber) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        setProgressMessage("Login in progress");
        Login login = new Login();
        login.setPhone(phoneNumber);
        login.setCountryShortName("");
        new RegisterApiCall(this).login(UserUtils.getDeviceId(), login);
    }

    @Override
    protected boolean validate() {
        AppUtils.hideKeyBoard(this);
        boolean isValid = true;
        Log.e("country code: ", ccp.getSelectedCountryCodeWithPlus());
        edt_phoneNo.setError(null);
        //countryCode = edt_phone_code.getText().toString();
        countryCode = ccp.getSelectedCountryCodeWithPlus();
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError(getString(R.string.error_mobile_blank));
            isValid = false;
        } else if (countryCode.equals("+91") && edt_phoneNo.getText().toString().length() != 10) {
            edt_phoneNo.setError(getString(R.string.error_mobile_no_length));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.d(TAG, "profile :" + profile.toString());
        AppInitialize.commitProfile(profile, email, auth);
        AppInitialize.setPreviousUserQID(profile.getQueueUserId());

        if (getIntent().getBooleanExtra("fromLogin", false)) {
            // To refresh the launch activity
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();//close the current activity
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej) {
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
                Intent in = new Intent(LoginActivity.this, RegistrationActivity.class);
                in.putExtra("mobile_no", verifiedMobileNo);
                in.putExtra("country_code", countryCode);
                in.putExtra("countryShortName", countryShortName);
                startActivity(in);
                dismissProgress();
                finish();//close the current activity
            } else if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.getCode())) {
                new CustomToast().showToast(this, getString(R.string.error_account_block));
                dismissProgress();
                finish();//close the current activity
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
    }

    @Override
    public void authenticationFailure() {

    }
}
