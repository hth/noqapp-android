package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.beans.body.ChangeMailOTP;
import com.noqapp.android.client.presenter.MigrateEmailPresenter;
import com.noqapp.android.client.presenter.beans.body.MigrateMail;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeEmailActivity extends BaseActivity implements View.OnClickListener, MigrateEmailPresenter,ProfilePresenter {

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_header)
    protected TextView tv_header;
    @BindView(R.id.edt_email)
    protected EditText edt_email;
    @BindView(R.id.edt_otp)
    protected EditText edt_otp;
    @BindView(R.id.btn_verify_email)
    protected Button btn_verify_email;
    @BindView(R.id.btn_validate_otp)
    protected Button btn_validate_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        ButterKnife.bind(this);
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.change_email));
        btn_verify_email.setOnClickListener(this);
        btn_validate_otp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_validate_otp:
                if (TextUtils.isEmpty(edt_otp.getText())) {
                    edt_otp.setError(getString(R.string.error_otp_blank));
                } else {
                    progressDialog.setMessage("OTP validation in progress...");
                    progressDialog.show();
                    new AppUtilities().hideKeyBoard(ChangeEmailActivity.this);
                    ChangeMailOTP changeMailOTP = new ChangeMailOTP();
                    changeMailOTP.setUserId(edt_email.getText().toString());
                    changeMailOTP.setMailOTP(edt_otp.getText().toString());
                    ProfileModel profileModel = new ProfileModel();
                    profileModel.setProfilePresenter(this);
                    profileModel.migrateMail(UserUtils.getEmail(), UserUtils.getAuth(), changeMailOTP);
                }
            break;
            case R.id.btn_verify_email:
                if (TextUtils.isEmpty(edt_email.getText())) {
                    edt_email.setError(getString(R.string.error_email_blank));
                } else if (isValidEmail(edt_email.getText())) {
                    progressDialog.setMessage("Email migration in progress...");
                    progressDialog.show();
                    new AppUtilities().hideKeyBoard(ChangeEmailActivity.this);
                    MigrateMail migrateMail = new MigrateMail();
                    migrateMail.setMail(edt_email.getText().toString());
                    ProfileModel profileModel = new ProfileModel();
                    profileModel.setMigrateEmailPresenter(this);
                    profileModel.changeMail(UserUtils.getEmail(), UserUtils.getAuth(), migrateMail);
                } else {
                    edt_email.setError(getString(R.string.error_invalid_email));
                }
                break;

        }
    }

    @Override
    public void migrateEmailResponse(JsonResponse jsonResponse) {
        Log.e("Email migrate:", jsonResponse.toString());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            btn_validate_otp.setVisibility(View.VISIBLE);
            edt_otp.setVisibility(View.VISIBLE);
            edt_email.setVisibility(View.GONE);
            btn_verify_email.setVisibility(View.GONE);
            tv_header.setText("Verification code");
            Toast.makeText(this,"Verification code is sent to above email address",Toast.LENGTH_LONG).show();
        }else {
            //Rejected from  server
            ErrorEncounteredJson eej = jsonResponse.getError();
            if (null != eej) {
                ShowAlertInformation.showThemeDialog(this, eej.getSystemError(), eej.getReason());
            }
        }
        dismissProgress();
    }

    @Override
    public void migrateEmailError() {
        dismissProgress();
    }

    @Override
    public void migrateEmailError(String error) {
        dismissProgress();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        if (profile.getError() == null) {
            Log.d(ChangeEmailActivity.class.getSimpleName(), "profile :" + profile.toString());
            NoQueueBaseActivity.commitProfile(profile, email, auth);
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
    public void profileError(String error) {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
    }

    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
