package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.CustomCalendar;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.views.activities.DatePickerActivity;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.RegisterApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.Registration;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;

import java.util.Random;
import java.util.TimeZone;

public class RegistrationActivity extends BaseActivity implements ProfilePresenter,
        View.OnClickListener {

    public interface RegisterCallBack {
        void userRegistered(JsonProfile jsonProfile);
    }

    public static RegisterCallBack registerCallBack;
    private final String TAG = RegistrationActivity.class.getSimpleName();
    public String gender = "";
    private EditText edt_phoneNo;
    private EditText edt_Name;
    private EditText edt_Mail;
    private TextView tv_birthday;
    private EditText edt_pwd;
    private EditText edt_confirm_pwd;
    private TextView tv_male;
    private TextView tv_female;
    private TextView tv_transgender;
    private LinearLayout ll_pwd;
    private Button btnRegistration;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initActionsViews(false);
        tv_toolbar_title.setText("Register");

        edt_phoneNo = findViewById(R.id.edt_phone);
        edt_Name = findViewById(R.id.edt_name);
        edt_Mail = findViewById(R.id.edt_email);
        tv_birthday = findViewById(R.id.tv_birthday);
        edt_pwd = findViewById(R.id.edt_pwd);
        edt_confirm_pwd = findViewById(R.id.edt_confirm_pwd);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);
        tv_transgender = findViewById(R.id.tv_transgender);
        ll_pwd = findViewById(R.id.ll_pwd);
        btnRegistration = findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(view -> action_Registration());
        tv_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_transgender.setOnClickListener(this);
        edt_phoneNo.setEnabled(false);
        onClick(tv_male);
        String phoneNumber = getIntent().getStringExtra("mobile_no");
        if (!TextUtils.isEmpty(phoneNumber)) {
            edt_phoneNo.setEnabled(false);
            edt_phoneNo.setText(phoneNumber);
        }
        edt_pwd.setText(generatePassword());
        edt_confirm_pwd.setText(edt_pwd.getText().toString());
    }

    public void action_Registration() {
        if (validate()) {
//            btnRegistration.setBackgroundResource(R.drawable.button_drawable_red);
//            btnRegistration.setTextColor(Color.WHITE);
            if (new NetworkUtil(this).isOnline()) {
                setProgressMessage("Registration in progress...");
                setProgressCancel(false);
                showProgress();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                callRegistrationAPI();
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        if (profile.getError() == null) {
            Log.d(TAG, "profile :" + profile.toString());
            if (null != registerCallBack)
                registerCallBack.userRegistered(profile);
            finish();

        } else {
            //Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (null != eej) {
                ShowAlertInformation.showThemeDialog(this, eej.getSystemError(), eej.getReason());
            }
        }
        dismissProgress();
    }

    @Override
    public void profileError() {
        dismissProgress();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_DATE_PICKER && resultCode == Activity.RESULT_OK) {
            String date = data.getStringExtra("result");
            if (!TextUtils.isEmpty(date) && CommonHelper.isDateBeforeToday(this, date))
                tv_birthday.setText(date);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_birthday) {
            AppUtils.hideKeyBoard(this);
            CustomCalendar customCalendar = new CustomCalendar(RegistrationActivity.this);
            customCalendar.setDateSelectListener(new CustomCalendar.DateSelectListener() {
                @Override
                public void calendarDate(String date) {
                    tv_birthday.setText(date);
                }
            });
            customCalendar.showDobCalendar();

//            Intent in = new Intent(RegistrationActivity.this, DatePickerActivity.class);
//            startActivityForResult(in, Constants.RC_DATE_PICKER);
        } else if (v == tv_male) {
            gender = "M";
            tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_transgender.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setBackgroundColor(ContextCompat.getColor(RegistrationActivity.this, R.color.review_color));
            tv_male.setText(getString(R.string.male));
            tv_male.setTextColor(Color.WHITE);
            tv_female.setTextColor(Color.BLACK);
            tv_transgender.setTextColor(Color.BLACK);
        } else if (v == tv_female) {
            gender = "F";
            tv_female.setBackgroundColor(ContextCompat.getColor(RegistrationActivity.this, R.color.review_color));
            tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_transgender.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setTextColor(Color.BLACK);
            tv_female.setTextColor(Color.WHITE);
            tv_transgender.setTextColor(Color.BLACK);
            tv_female.setText(getString(R.string.female));
        } else if (v == tv_transgender) {
            gender = "T";
            tv_transgender.setBackgroundColor(ContextCompat.getColor(RegistrationActivity.this, R.color.review_color));
            tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setTextColor(Color.BLACK);
            tv_female.setTextColor(Color.BLACK);
            tv_transgender.setTextColor(Color.WHITE);
            tv_transgender.setText(getString(R.string.transgender));
        }
    }

    private boolean validate() {
        //btnRegistration.setBackgroundResource(R.drawable.button_drawable);
        // btnRegistration.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        boolean isValid = true;
        edt_Name.setError(null);
        edt_Mail.setError(null);
        tv_birthday.setError(null);
        edt_pwd.setError(null);
        AppUtils.hideKeyBoard(this);
        String errorMsg = "";
        if (TextUtils.isEmpty(edt_Name.getText().toString())) {
            edt_Name.setError(getString(R.string.error_name_blank));
            errorMsg = getString(R.string.error_name_blank);
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Name.getText().toString()) && edt_Name.getText().toString().length() < 3) {
            edt_Name.setError(getString(R.string.error_name_length));
            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = getString(R.string.error_name_length);
            }
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Mail.getText().toString())) {
            if (!CommonHelper.isValidEmail(edt_Mail.getText().toString())) {
                edt_Mail.setError(getString(R.string.error_invalid_email));
                if (TextUtils.isEmpty(errorMsg)) {
                    errorMsg = getString(R.string.error_invalid_email);
                }
                isValid = false;
            }
        }
        if (TextUtils.isEmpty(tv_birthday.getText().toString())) {
            tv_birthday.setError(getString(R.string.error_dob_blank));
            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = getString(R.string.error_dob_blank);
            }
            isValid = false;
        } else {
            if (TextUtils.isEmpty(AppUtils.convertDOBToValidFormat(tv_birthday.getText().toString()))) {
                tv_birthday.setError(getString(R.string.error_dob_format));
                if (TextUtils.isEmpty(errorMsg)) {
                    errorMsg = getString(R.string.error_dob_format);
                }
                isValid = false;
            }
        }

        if (!TextUtils.isEmpty(errorMsg)) {
            new CustomToast().showToast(this, errorMsg);
        }
        return isValid;
    }

    public void callRegistrationAPI() {
        String phoneNo = edt_phoneNo.getText().toString();
        String name = edt_Name.getText().toString();
        String mail = edt_Mail.getText().toString();
        String birthday = tv_birthday.getText().toString();
        TimeZone tz = TimeZone.getDefault();
        Log.d(TAG, "TimeZone=" + tz.getDisplayName(false, TimeZone.SHORT) + " TimezoneId=" + tz.getID());

        Registration registration = new Registration();
        registration.setPhone(phoneNo);
        registration.setFirstName(name);
        registration.setMail(mail);
        registration.setPassword(edt_pwd.getText().toString());
        registration.setBirthday(AppUtils.convertDOBToValidFormat(birthday));
        registration.setGender(gender);
        registration.setTimeZoneId(tz.getID());
        registration.setCountryShortName(getIntent().getStringExtra("countryShortName"));
        registration.setInviteCode("");

        RegisterApiCalls registerApiCalls = new RegisterApiCalls(this);
        registerApiCalls.register(UserUtils.getDeviceId(), registration);
    }

    private String generatePassword() {
        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);
        return String.valueOf(n);
    }
}
