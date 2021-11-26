package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.ClientProfileApiImpl;
import com.noqapp.android.client.presenter.MigrateEmailPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.MigrateMail;
import com.noqapp.android.client.presenter.beans.body.mail.ChangeMailOTP;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;

public class ChangeEmailActivity extends BaseActivity implements View.OnClickListener, MigrateEmailPresenter, ProfilePresenter {
    private final String TAG = ChangeEmailActivity.class.getSimpleName();

    private TextView tv_header;
    private TextView tv_msg;
    private EditText edt_email;
    private EditText edt_otp;
    private Button btn_verify_email;
    private Button btn_validate_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        tv_header = findViewById(R.id.tv_header);
        tv_msg = findViewById(R.id.tv_msg);
        edt_email = findViewById(R.id.edt_email);
        edt_otp = findViewById(R.id.edt_otp);
        btn_verify_email = findViewById(R.id.btn_verify_email);
        btn_validate_otp = findViewById(R.id.btn_validate_otp);
        initActionsViews(true);
        if (getIntent().getBooleanExtra("isValidated", false)) {
            tv_toolbar_title.setText(getString(R.string.change_email));
            btn_verify_email.setText("Start email migration");
        } else {
            tv_toolbar_title.setText(getString(R.string.verify_email));
            edt_email.setText(getIntent().getStringExtra("email"));
        }
        btn_verify_email.setOnClickListener(this);
        btn_validate_otp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_validate_otp:
                if (TextUtils.isEmpty(edt_otp.getText())) {
                    edt_otp.setError(getString(R.string.error_otp_blank));
                } else {
                    setProgressMessage("OTP validation in progress...");
                    showProgress();
                    AppUtils.hideKeyBoard(ChangeEmailActivity.this);
                    ChangeMailOTP changeMailOTP = new ChangeMailOTP();
                    changeMailOTP.setUserId(edt_email.getText().toString());
                    changeMailOTP.setMailOTP(edt_otp.getText().toString());
                    ClientProfileApiImpl clientProfileApiImpl = new ClientProfileApiImpl();
                    clientProfileApiImpl.setProfilePresenter(this);
                    clientProfileApiImpl.migrateMail(UserUtils.getEmail(), UserUtils.getAuth(), changeMailOTP);
                }
                break;
            case R.id.btn_verify_email:
                if (TextUtils.isEmpty(edt_email.getText())) {
                    edt_email.setError(getString(R.string.error_email_blank));
                } else if (CommonHelper.isValidEmail(edt_email.getText())) {
                    setProgressMessage("Request OTP in progress...");
                    showProgress();
                    AppUtils.hideKeyBoard(ChangeEmailActivity.this);
                    MigrateMail migrateMail = new MigrateMail();
                    migrateMail.setMail(edt_email.getText().toString());
                    ClientProfileApiImpl clientProfileModel = new ClientProfileApiImpl();
                    clientProfileModel.setMigrateEmailPresenter(this);
                    clientProfileModel.changeMail(UserUtils.getEmail(), UserUtils.getAuth(), migrateMail);
                } else {
                    edt_email.setError(getString(R.string.error_invalid_email));
                }
                break;
        }
    }

    @Override
    public void migrateEmailResponse(JsonResponse jsonResponse) {
        Log.d(TAG, "Email migrate: " + jsonResponse.toString());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            btn_validate_otp.setVisibility(View.VISIBLE);
            edt_otp.setVisibility(View.VISIBLE);
            tv_msg.setVisibility(View.VISIBLE);
            edt_email.setVisibility(View.GONE);
            btn_verify_email.setVisibility(View.GONE);
            tv_header.setText("Verification code");
            new CustomToast().showToast(this, "Verification code is sent to above email address");
        } else {
            //Rejected from  server
            ErrorEncounteredJson eej = jsonResponse.getError();
            if (null != eej) {
                ShowAlertInformation.showThemeDialog(this, eej.getSystemError(), eej.getReason());
            }
        }
        dismissProgress();
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        if (profile.getError() == null) {
            Log.d(TAG, "profile :" + profile.toString());
            AppInitialize.commitProfile(profile, email, auth);
            finish();
        } else {
            //Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            ShowAlertInformation.showThemeDialog(this, eej.getSystemError(), eej.getReason());
        }
        dismissProgress();
    }

    @Override
    public void profileError() {
        dismissProgress();
    }
}
