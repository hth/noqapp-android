package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ClientProfileApiCall;
import com.noqapp.android.client.presenter.beans.body.MigratePhone;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import java.util.TimeZone;

/**
 * Created by chandra on 5/7/17.
 */
public class MigrateActivity extends OTPActivity {

    private final String TAG = MigrateActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        activity = this;
        tv_toolbar_title.setText("Migrate Number");
        btn_login.setText("Start Migration");
    }

    @Override
    protected void callApi(String phoneNumber) {
        setProgressMessage("Account migration in progress");
        TimeZone tz = TimeZone.getDefault();
        MigratePhone migratePhone = new MigratePhone();
        migratePhone.setCountryShortName(countryShortName);
        migratePhone.setTimeZoneId(tz.getID());
        migratePhone.setPhone(phoneNumber);
        ClientProfileApiCall clientProfileApiCall = new ClientProfileApiCall();
        clientProfileApiCall.setProfilePresenter(this);
        clientProfileApiCall.migrate(UserUtils.getEmail(), UserUtils.getAuth(), migratePhone);
    }

    @Override
    protected boolean validate() {
        AppUtils.hideKeyBoard(this);
        boolean isValid = true;
        edt_phoneNo.setError(null);
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError(getString(R.string.error_mobile_blank));
            isValid = false;
        } else if (countryCode.equals("+91") && edt_phoneNo.getText().toString().length() != 10) {
            edt_phoneNo.setError(getString(R.string.error_mobile_no_length));
            isValid = false;
        } else {
            if (PhoneFormatterUtil.phoneNumberWithCountryCode(edt_phoneNo.getText().toString(), countryShortName).
                    equals(PhoneFormatterUtil.phoneNumberWithCountryCode(MyApplication.getPhoneNo(), MyApplication.getCountryShortName()))) {
                edt_phoneNo.setError(getString(R.string.error_mobile_no_same));
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.d(TAG, "profile :" + profile.toString());
        MyApplication.commitProfile(profile, email, auth);
        finish();//close the current activity
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {

    }
}
