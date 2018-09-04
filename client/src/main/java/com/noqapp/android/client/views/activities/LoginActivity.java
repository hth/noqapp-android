package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.RegisterModel;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class LoginActivity extends OTPActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        tv_toolbar_title.setText("Login");
    }

    @Override
    protected void callApi(String phoneNumber) {
        progressDialog.setMessage("Login in progress");
        Login login = new Login();
        login.setPhone(phoneNumber);
        login.setCountryShortName("");
        new RegisterModel(this).login(UserUtils.getDeviceId(), login);
    }

    @Override
    protected boolean validate() {
        new AppUtilities().hideKeyBoard(this);
        boolean isValid = true;
        edt_phoneNo.setError(null);
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError(getString(R.string.error_mobile_blank));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        if (profile.getError() == null) {
            Log.d(TAG, "profile :" + profile.toString());
            NoQueueBaseActivity.commitProfile(profile, email, auth);
            if (getIntent().getBooleanExtra("fromLogin", false)) {
                // To refresh the launch activity
                Intent intent = new Intent(this, LaunchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finish();//close the current activity
            dismissProgress();
        } else {
            // Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
                Intent in = new Intent(LoginActivity.this, RegistrationActivity.class);
                in.putExtra("mobile_no", verifiedMobileNo);
                in.putExtra("country_code", countryCode);
                in.putExtra("countryShortName", countryShortName);
                startActivity(in);
                dismissProgress();
                finish();//close the current activity
            }
        }
    }

}
