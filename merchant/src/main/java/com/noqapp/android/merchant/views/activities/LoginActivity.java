package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.RegisterApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.Login;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity implements ProfilePresenter {
    public interface LoginCallBack {
        void userFound(JsonProfile jsonProfile);
    }

    public static LoginCallBack loginCallBack;
    private final String TAG = LoginActivity.class.getSimpleName();

    protected enum STATE {
        STATE_INITIALIZED,
        STATE_CODE_SENT,
        STATE_VERIFY_FAILED,
        STATE_TOO_MANY_REQUEST,
        STATE_VERIFY_SUCCESS,
        STATE_SIGN_IN_FAILED,
        STATE_SIGN_IN_SUCCESS
    }

    private EditText edt_phoneNo;
    private Button btn_login;
    private Button btn_verify_phone;
    private EditText edt_verification_code;
    private CountryCodePicker ccp;
    private TextView tv_detail;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private String verifiedMobileNo;
    private FirebaseAuth mAuth;
    private String countryCode = "";
    private String countryShortName = "";
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initActionsViews(false);
        tv_toolbar_title.setText("Verify Mobile Number");
        edt_phoneNo = findViewById(R.id.edt_phone);
        btn_login = findViewById(R.id.btn_login);
        btn_verify_phone = findViewById(R.id.btn_verify_phone);
        edt_verification_code = findViewById(R.id.edt_verification_code);
        ccp = findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(() -> {
            countryCode = ccp.getSelectedCountryCodeWithPlus();
            countryShortName = ccp.getDefaultCountryNameCode().toUpperCase();
        });
        tv_detail = findViewById(R.id.tv_detail);
        btn_login.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            action_Login();
        });
        btn_verify_phone.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            btnVerifyClick();
        });
        setProgressMessage("Login in progress");
        mAuth = FirebaseAuth.getInstance();
        updateUI(STATE.STATE_INITIALIZED);
        String c_codeValue = LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        Log.v("country code", "" + c_code);
        countryCode = "+" + c_code;
        countryShortName = c_codeValue.toUpperCase();
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        countryCode = ccp.getSelectedCountryCodeWithPlus();
        countryShortName = ccp.getDefaultCountryNameCode().toUpperCase();
        edt_phoneNo.setText(getIntent().getStringExtra("phone_no"));
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
                updateUI(STATE.STATE_VERIFY_SUCCESS, credential);
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
                    updateUI(STATE.STATE_VERIFY_FAILED);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e("OTP process: ", "Quota exceeded.");
                    updateUI(STATE.STATE_TOO_MANY_REQUEST);
                } else {
                    updateUI(STATE.STATE_VERIFY_FAILED);
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                btn_login.setText(getString(R.string.resend_otp));
                btn_login.setTextColor(Color.parseColor("#333333"));
                btn_login.setPaintFlags(btn_login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                btn_login.setBackground(null);
                btn_login.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                btn_login.requestLayout();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                // Update UI
                updateUI(STATE.STATE_CODE_SENT);
                //setProgressMessage("OTP Generated and sent to the above mobile number");
                dismissProgress();
            }
        };
    }

    private void action_Login() {
        if (validate()) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showProgress();
                setProgressMessage("Generating OTP");
                countryCode = ccp.getSelectedCountryCodeWithPlus();
                countryShortName = ccp.getDefaultCountryNameCode().toUpperCase();
                startPhoneNumberVerification(countryCode + edt_phoneNo.getText().toString());

//                Answers.getInstance().logLogin(new LoginEvent()
//                        .putMethod("Phone")
//                        .putSuccess(true));
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }

    private void callLoginAPI(String phoneNumber) {
        Login login = new Login();
        login.setPhone(phoneNumber);
        login.setCountryShortName("");
        RegisterApiCalls registerApiCalls = new RegisterApiCalls(this);
        registerApiCalls.login(UserUtils.getDeviceId(), login);
    }

    private boolean validate() {
        AppUtils.hideKeyBoard(this);
        boolean isValid = true;
        edt_phoneNo.setError(null);
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError(getString(R.string.error_mobile_blank));
            isValid = false;
        }
        return isValid;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        try {
            // [START start_phone_auth]
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            // [END start_phone_auth]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        setProgressMessage("Validating OTP");
        showProgress();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        Log.v(TAG, user.toString() + "mobile :" + user.getPhoneNumber());
                        verifiedMobileNo = user.getPhoneNumber();
                        updateUI(STATE.STATE_SIGN_IN_SUCCESS, user);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            edt_verification_code.setError("Invalid code.");
                        }
                        // Update UI
                        updateUI(STATE.STATE_SIGN_IN_FAILED);
                    }
                    dismissProgress();
                });
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.d(TAG, "profile :" + profile.toString());
        if (null != loginCallBack) {
            loginCallBack.userFound(profile);
        }
        finish();//close the current activity
        dismissProgress();
    }

    @Override
    public void profileError() {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
            Intent in = new Intent(LoginActivity.this, RegistrationActivity.class);
            in.putExtra("mobile_no", verifiedMobileNo);
            in.putExtra("country_code", countryCode);
            in.putExtra("countryShortName", countryShortName);
            startActivity(in);
            dismissProgress();
            finish();//close the current activity
        } else {
            new ErrorResponseHandler().processError(this, eej);
        }
    }

    public void btnVerifyClick() {
        edt_verification_code.setError(null);
        String code = edt_verification_code.getText().toString();
        if (TextUtils.isEmpty(code)) {
            edt_verification_code.setError("Cannot be empty.");
            return;
        }
        if (mVerificationId != null) {
            showProgress();
            AppUtils.hideKeyBoard(this);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            //Toast.makeText(this,"mVerificationId is null: ", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI(STATE uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(STATE uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(STATE uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(STATE uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                AppUtils.showViews(btn_login, edt_phoneNo);
                AppUtils.hideViews(btn_verify_phone, edt_verification_code);
                tv_detail.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                AppUtils.showViews(btn_login, edt_phoneNo, btn_verify_phone, edt_verification_code);
                tv_detail.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                //enableViews(btn_login, edt_phoneNo, btn_verify_phone, edt_verification_code);
                tv_detail.setText(R.string.status_verification_failed);
                break;
            case STATE_TOO_MANY_REQUEST:
                tv_detail.setText(R.string.status_too_many_request);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                AppUtils.showViews(edt_phoneNo, btn_verify_phone, edt_verification_code);
                AppUtils.hideViews(btn_login);
                tv_detail.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        edt_verification_code.setText(cred.getSmsCode());
                    } else {
                        edt_verification_code.setText(R.string.instant_validation);
                        AppUtils.hideViews(edt_verification_code);
                    }
                }
                break;
            case STATE_SIGN_IN_FAILED:
                // No-op, handled by sign-in check
                tv_detail.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGN_IN_SUCCESS:
                // Np-op, handled by sign-in check

                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    showProgress();
                    callLoginAPI(user.getPhoneNumber());
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                    dismissProgress();
                }
                break;
        }
    }
}
