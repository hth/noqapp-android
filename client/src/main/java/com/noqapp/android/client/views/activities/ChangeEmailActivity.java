package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.MigrateEmailPresenter;
import com.noqapp.android.client.presenter.beans.body.MigrateMail;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonResponse;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeEmailActivity extends BaseActivity implements View.OnClickListener, MigrateEmailPresenter {

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
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
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(edt_email.getText())) {
            edt_email.setError(getString(R.string.error_email_blank));
        } else if (!TextUtils.isEmpty(edt_email.getText()) && !isValidEmail(edt_email.getText())) {
            edt_email.setError(getString(R.string.error_invalid_email));
        } else {
            progressDialog.setMessage("Email migration in progress...");
            progressDialog.show();
            MigrateMail migrateMail = new MigrateMail();
            migrateMail.setMail(edt_email.getText().toString());
            ProfileModel profileModel = new ProfileModel();
            profileModel.setMigrateEmailPresenter(this);
            profileModel.changeMail(UserUtils.getEmail(), UserUtils.getAuth(), migrateMail);
        }
    }

    @Override
    public void migrateEmailResponse(JsonResponse jsonResponse) {
        Log.e("Email migrate:", jsonResponse.toString());
    }

    @Override
    public void migrateEmailError() {
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
