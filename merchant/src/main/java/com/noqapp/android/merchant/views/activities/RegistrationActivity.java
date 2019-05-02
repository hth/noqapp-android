package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.RegisterApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.Registration;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RegistrationActivity extends AppCompatActivity implements ProfilePresenter, View.OnClickListener {

    public interface RegisterCallBack {
        void passPhoneNo(JsonProfile jsonProfile);
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
    private LinearLayout ll_pwd;
    private Button btnRegistration;
    private long mLastClickTime = 0;
    private DatePickerDialog fromDatePickerDialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initProgress();

        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Register");

        edt_phoneNo = findViewById(R.id.edt_phone);
        edt_Name = findViewById(R.id.edt_name);
        edt_Mail = findViewById(R.id.edt_email);
        tv_birthday = findViewById(R.id.tv_birthday);
        edt_pwd = findViewById(R.id.edt_pwd);
        edt_confirm_pwd = findViewById(R.id.edt_confirm_pwd);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);
        ll_pwd = findViewById(R.id.ll_pwd);
        btnRegistration = findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_Registration();
            }
        });
        tv_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        edt_phoneNo.setEnabled(false);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);

                if (date_diff < 0) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.error_invalid_date), Toast.LENGTH_LONG).show();
                    tv_birthday.setText("");
                } else {
                    tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        onClick(tv_male);
        String phno = getIntent().getStringExtra("mobile_no");
        if (!TextUtils.isEmpty(phno)) {
            edt_phoneNo.setEnabled(false);
            edt_phoneNo.setText(phno);
        }

        edt_Mail.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == 0) {
                    ll_pwd.setVisibility(View.GONE);
                    edt_pwd.setText("");
                    edt_confirm_pwd.setText("");
                } else {
                    ll_pwd.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    public void action_Registration() {
        if (validate()) {
            btnRegistration.setBackgroundResource(R.drawable.button_drawable_red);
            btnRegistration.setTextColor(Color.WHITE);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
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
            registerCallBack.passPhoneNo(profile);
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
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void onClick(View v) {
        if (v == tv_birthday) {
            new AppUtils().hideKeyBoard(this);
            fromDatePickerDialog.show();
        } else if (v == tv_male) {
            gender = "M";
            tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setBackgroundResource(R.drawable.button_drawable_red_square);
            tv_male.setText(getString(R.string.male));
            tv_male.setTextColor(Color.WHITE);
            tv_female.setTextColor(Color.BLACK);
        } else if (v == tv_female) {
            gender = "F";
            tv_female.setBackgroundResource(R.drawable.button_drawable_red_square);
            tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setTextColor(Color.BLACK);
            tv_female.setTextColor(Color.WHITE);
            tv_female.setText(getString(R.string.female));
        }
    }

    private boolean validate() {
        btnRegistration.setBackgroundResource(R.drawable.button_drawable);
        btnRegistration.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        boolean isValid = true;
        edt_Name.setError(null);
        edt_Mail.setError(null);
        tv_birthday.setError(null);
        edt_pwd.setError(null);
        new AppUtils().hideKeyBoard(this);
        String errorMsg = "";
        if (TextUtils.isEmpty(edt_Name.getText().toString())) {
            edt_Name.setError(getString(R.string.error_name_blank));
            errorMsg = getString(R.string.error_name_blank);
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Name.getText().toString()) && edt_Name.getText().toString().length() < 4) {
            edt_Name.setError(getString(R.string.error_name_length));
            if (TextUtils.isEmpty(errorMsg))
                errorMsg = getString(R.string.error_name_length);
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Mail.getText().toString())) {

            if (!new CommonHelper().isValidEmail(edt_Mail.getText().toString())) {
                edt_Mail.setError(getString(R.string.error_invalid_email));
                if (TextUtils.isEmpty(errorMsg))
                    errorMsg = getString(R.string.error_invalid_email);
                isValid = false;
            }
            if (TextUtils.isEmpty(edt_pwd.getText().toString())) {
                edt_pwd.setError(getString(R.string.error_pwd_blank));
                if (TextUtils.isEmpty(errorMsg))
                    errorMsg = getString(R.string.error_pwd_blank);
                isValid = false;
            } else {
                if (edt_pwd.getText().toString().length() < 6) {
                    edt_pwd.setError(getString(R.string.error_pwd_length));
                    if (TextUtils.isEmpty(errorMsg))
                        errorMsg = getString(R.string.error_pwd_length);
                    isValid = false;
                } else if (!edt_pwd.getText().toString().equals(edt_confirm_pwd.getText().toString())) {
                    edt_pwd.setError(getString(R.string.error_pwd_not_match));
                    if (TextUtils.isEmpty(errorMsg))
                        errorMsg = getString(R.string.error_pwd_not_match);
                    isValid = false;
                }
            }
        }
        if (TextUtils.isEmpty(tv_birthday.getText().toString())) {
            tv_birthday.setError(getString(R.string.error_dob_blank));
            if (TextUtils.isEmpty(errorMsg))
                errorMsg = getString(R.string.error_dob_blank);
            isValid = false;
        }

        if (!TextUtils.isEmpty(errorMsg))
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
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

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
