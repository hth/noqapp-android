package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.beans.body.MigrateProfile;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.TimeZone;

public class MigrateActivity extends OTPActivity {

    private final String TAG = MigrateActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        tv_toolbar_title.setText("Migrate No");
        btn_login.setText("Start Migrate");
    }

    @Override
    protected void callApi(String phoneNumber) {
        progressDialog.setMessage("Account migration in progress");
        TimeZone tz = TimeZone.getDefault();
        Log.d(TAG, "TimeZone=" + tz.getDisplayName(false, TimeZone.SHORT) + " TimezoneId=" + tz.getID());

        MigrateProfile migrateProfile = new MigrateProfile();
        migrateProfile.setCountryShortName(countryShortName);
        migrateProfile.setTimeZoneId(tz.getID());
        migrateProfile.setPhone(phoneNumber);
        ProfileModel profileModel = new ProfileModel();
        profileModel.setProfilePresenter(this);
        profileModel.migrate(UserUtils.getEmail(), UserUtils.getAuth(), migrateProfile);
    }

    @Override
    protected boolean validate() {
        new AppUtilities().hideKeyBoard(this);
        boolean isValid = true;
        edt_phoneNo.setError(null);
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError(getString(R.string.error_mobile_blank));
            isValid = false;
        } else {
            if (PhoneFormatterUtil.phoneNumberWithCountryCode(edt_phoneNo.getText().toString(), countryShortName).
                    equals(PhoneFormatterUtil.phoneNumberWithCountryCode(NoQueueBaseActivity.getPhoneNo(), NoQueueBaseActivity.getCountryShortName()))) {
                edt_phoneNo.setError(getString(R.string.error_mobile_no_same));
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        if (profile.getError() == null) {
            Log.d(TAG, "profile :" + profile.toString());
            NoQueueBaseActivity.commitProfile(profile, email, auth);
            finish();//close the current activity
            dismissProgress();
        } else {
            // Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
        }
        dismissProgress();
    }

}
