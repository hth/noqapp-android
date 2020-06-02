package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import java.util.concurrent.TimeUnit;

public abstract class OTPActivity extends BaseActivity implements ProfilePresenter {
    protected String TAG = LoginActivity.class.getSimpleName();
    protected final int STATE_INITIALIZED = 1;
    protected final int STATE_CODE_SENT = 2;
    protected final int STATE_VERIFY_FAILED = 3;
    protected final int STATE_VERIFY_SUCCESS = 4;
    protected final int STATE_SIGN_IN_FAILED = 5;
    protected final int STATE_SIGN_IN_SUCCESS = 6;
    protected PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    protected String mVerificationId;
    protected FirebaseAuth mAuth;
    protected String countryCode = "";
    protected String countryShortName = "";
    protected String verifiedMobileNo;
    protected Activity activity;
    protected ImageView actionbarBack;
    protected TextView tv_toolbar_title;
    protected EditText edt_phoneNo;
    protected Button btn_login;
    protected Button btn_verify_phone;
    protected EditText edt_phone_code;

    protected TextView tv_detail;
    protected CountryCodePicker ccp;

    protected abstract void callApi(String phoneNumber);

    protected abstract boolean validate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actionbarBack = findViewById(R.id.actionbarBack);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        edt_phoneNo = findViewById(R.id.edt_phone);
        btn_login = findViewById(R.id.btn_login);
        btn_verify_phone = findViewById(R.id.btn_verify_phone);
        edt_phone_code = findViewById(R.id.edt_phone_code);
        tv_detail = findViewById(R.id.tv_detail);
        ccp = findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(() -> {
            countryCode = ccp.getSelectedCountryCodeWithPlus();
            countryShortName = ccp.getDefaultCountryNameCode().toUpperCase();
        });
        btn_login.setOnClickListener((View v) -> {
            actionLogin();
        });
        btn_verify_phone.setOnClickListener((View v) -> {
            btnVerifyClick();
        });
        actionbarBack.setOnClickListener((View v) -> {
            finish();
        });
        mAuth = FirebaseAuth.getInstance();
        updateUI(STATE_INITIALIZED);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String c_codeValue = tm.getNetworkCountryIso();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        Log.v("country code", "" + c_code);
        countryCode = "+" + c_code;
        countryShortName = c_codeValue.toUpperCase();
        countryCode = ccp.getSelectedCountryCodeWithPlus();
        countryShortName = ccp.getDefaultCountryNameCode().toUpperCase();
        // edt_phone_code.setText(countryCode);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                dismissProgress();

                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                dismissProgress();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.e("OTP process: ", "Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e("OTP process: ", "Quota exceeded.");
                }
                // Show a message and update the UI
                updateUI(STATE_VERIFY_FAILED);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                btn_login.setText(getString(R.string.resend_otp));
                btn_login.setPaintFlags(btn_login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                btn_login.setBackground(null);
                btn_login.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                btn_login.requestLayout();
                setProgressMessage("OTP Generated");
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                // Update UI
                updateUI(STATE_CODE_SENT);

            }
        };
    }

    protected void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        setProgressMessage("Validating OTP");
        showProgress();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Log.v(TAG, user.toString() + "mobile :" + user.getPhoneNumber());
                            verifiedMobileNo = user.getPhoneNumber();
                            updateUI(STATE_SIGN_IN_SUCCESS, user);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                new CustomToast().showToast(activity, "Invalid code.");
                            }
                            // Update UI
                            updateUI(STATE_SIGN_IN_FAILED);
                            dismissProgress();
                        }

                    }
                });
    }

    protected void startPhoneNumberVerification(String phoneNumber) {
        try {
            // [START start_phone_auth]
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,              // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,       // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            // [END start_phone_auth]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    protected void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    protected void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                AppUtils.showViews(btn_login, edt_phoneNo);
                AppUtils.hideViews(btn_verify_phone, edt_phone_code);
                tv_detail.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                AppUtils.showViews(btn_login, edt_phoneNo, btn_verify_phone, edt_phone_code);

                tv_detail.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                // enableViews(btn_login, edt_phoneNo, btn_verify_phone, edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);
                tv_detail.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                AppUtils.showViews(edt_phoneNo, btn_verify_phone, edt_phone_code);
                AppUtils.hideViews(btn_login);
                tv_detail.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (!TextUtils.isEmpty(cred.getSmsCode()) && cred.getSmsCode().length() == 6) {
                        edt_phone_code.setText(cred.getSmsCode());
                    } else {
                        edt_phone_code.setText("");
                        AppUtils.hideViews(edt_phone_code);
                    }
                }
                break;
            case STATE_SIGN_IN_FAILED:
                // No-op, handled by sign-in check
                tv_detail.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGN_IN_SUCCESS:
                // Np-op, handled by sign-in check
                // Call login or migration API
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    callApi(user.getPhoneNumber());
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                    dismissProgress();
                }
                break;
        }
    }


    private boolean validateOTP(EditText... views) {
        boolean isValid = true;
        for (EditText v : views) {
            if (v.getText().toString().equals("")) {
                v.setError("Cannot be empty.");
                isValid = false;
            }
        }
        return isValid;
    }

    private void btnVerifyClick() {
        edt_phone_code.setError(null);
        if (!validateOTP(edt_phone_code)) {
            return;
        }
        if (mVerificationId != null) {
            showProgress();
            AppUtils.hideKeyBoard(this);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edt_phone_code.getText().toString());
            signInWithPhoneAuthCredential(credential);
        } else {
            //Toast.makeText(this,"mVerificationId is null: ", Toast.LENGTH_LONG).show();
        }
    }

    private void actionLogin() {
        if (validate()) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showProgress();
                setProgressMessage("Generating OTP");
                //countryCode = edt_phone_code.getText().toString();
                countryCode = ccp.getSelectedCountryCodeWithPlus();
                startPhoneNumberVerification(countryCode + edt_phoneNo.getText().toString());

                Bundle params = new Bundle();
                params.putBoolean("Phone", true);
                LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_LOGIN_SCREEN, params);

            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }

    @Override
    public void profileError() {
        dismissProgress();
    }
}
