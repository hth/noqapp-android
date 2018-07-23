package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

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

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import java.util.concurrent.TimeUnit;

public abstract class OTPActivity extends BaseActivity implements ProfilePresenter {
    protected String TAG = LoginActivity.class.getSimpleName();
    protected final int STATE_INITIALIZED = 1;
    protected final int STATE_CODE_SENT = 2;
    protected final int STATE_VERIFY_FAILED = 3;
    protected final int STATE_VERIFY_SUCCESS = 4;
    protected final int STATE_SIGN_IN_FAILED = 5;
    protected final int STATE_SIGN_IN_SUCCESS = 6;
    protected final int READ_AND_RECEIVE_SMS_PERMISSION_CODE = 101;
    protected final String[] READ_AND_RECEIVE_SMS_PERMISSION_PERMS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };
    protected PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    protected String mVerificationId;
    protected FirebaseAuth mAuth;
    protected String countryCode = "";
    protected String countryShortName = "";
    protected String verifiedMobileNo;
    protected Activity activity;

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.edt_phone)
    protected EditText edt_phoneNo;
    @BindView(R.id.btn_login)
    protected Button btn_login;
    @BindView(R.id.btn_verify_phone)
    protected Button btn_verify_phone;
    @BindView(R.id.edt_phone_code)
    protected EditText edt_phone_code;


    @BindView(R.id.edt_one)
    protected EditText edt_one;
    @BindView(R.id.edt_two)
    protected EditText edt_two;
    @BindView(R.id.edt_three)
    protected EditText edt_three;
    @BindView(R.id.edt_four)
    protected EditText edt_four;
    @BindView(R.id.edt_five)
    protected EditText edt_five;
    @BindView(R.id.edt_six)
    protected EditText edt_six;
    @BindView(R.id.ll_otp)
    protected LinearLayout ll_otp;


    @BindView(R.id.tv_detail)
    protected TextView tv_detail;

    protected abstract void callApi(String phoneNumber);
    protected abstract boolean validate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        updateUI(STATE_INITIALIZED);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String c_codeValue = tm.getNetworkCountryIso();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        Log.v("country code", "" + c_code);
        countryCode = "+" + c_code;
        countryShortName = c_codeValue.toUpperCase();
        edt_phone_code.setText(countryCode);
        addTextWatcher(edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
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
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                btn_login.setText(getString(R.string.resend_otp));
                btn_login.setPaintFlags(btn_login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                btn_login.setBackground(null);
                btn_login.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                btn_login.requestLayout();
                progressDialog.setMessage("OTP Generated");
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                // Update UI
                updateUI(STATE_CODE_SENT);

            }
        };
    }

    protected void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressDialog.setMessage("Validating OTP");
        progressDialog.show();
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
                                Toast.makeText(activity, "Invalid code.", Toast.LENGTH_LONG).show();
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
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            // [END start_phone_auth]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isReadAndReceiveSMSPermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int result_write = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    protected void requestReadAndReceiveSMSPermissionAllowed() {
        ActivityCompat.requestPermissions(
                this,
                READ_AND_RECEIVE_SMS_PERMISSION_PERMS,
                READ_AND_RECEIVE_SMS_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == READ_AND_RECEIVE_SMS_PERMISSION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //@TODO @Chandra update the country code dynamic
                startPhoneNumberVerification(countryCode + edt_phoneNo.getText().toString());
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //No permission allowed
                //Do nothing
            }
        }
    }


    protected void enableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    protected void disableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);
        }
    }

    protected void setErrorNull(EditText... views) {
        for (EditText v : views) {
            v.setError(null);
        }
    }

    protected void setTextBlank(EditText... views) {
        for (EditText v : views) {
            v.setText("");
        }
    }

    protected void addTextWatcher(EditText... views) {
        for (EditText v : views) {
            v.addTextChangedListener(new GenericTextWatcher(v));
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
                enableViews(btn_login, edt_phoneNo);
                disableViews(btn_verify_phone, edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);
                tv_detail.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(btn_login, edt_phoneNo, btn_verify_phone, edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);

                tv_detail.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(btn_login, edt_phoneNo, btn_verify_phone, edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);
                tv_detail.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                enableViews(edt_phoneNo, btn_verify_phone, edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);
                disableViews(btn_login);
                tv_detail.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (!TextUtils.isEmpty(cred.getSmsCode()) && cred.getSmsCode().length() ==6) {
                        edt_one.setText(cred.getSmsCode().charAt(0));
                        edt_two.setText(cred.getSmsCode().charAt(1));
                        edt_three.setText(cred.getSmsCode().charAt(2));
                        edt_four.setText(cred.getSmsCode().charAt(3));
                        edt_five.setText(cred.getSmsCode().charAt(4));
                        edt_six.setText(cred.getSmsCode().charAt(5));
                    } else {
                        setTextBlank(edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);
                        disableViews(edt_one, edt_two, edt_three, edt_four, edt_five, edt_six);
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

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.edt_one:
                    if (text.length() == 1)
                        edt_two.requestFocus();
                    break;
                case R.id.edt_two:
                    if (text.length() == 1)
                        edt_three.requestFocus();
                    break;
                case R.id.edt_three:
                    if (text.length() == 1)
                        edt_four.requestFocus();
                    break;
                case R.id.edt_four:
                    if (text.length() == 1)
                        edt_five.requestFocus();
                    break;
                case R.id.edt_five:
                    if (text.length() == 1)
                        edt_six.requestFocus();
                    break;
                case R.id.edt_six:
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }

    private boolean validateOTP(EditText... views){
        boolean isValid = true;
        for (EditText v : views) {
            if(v.getText().toString().equals("")) {
                v.setError("Cannot be empty.");
                isValid = false;
            }
        }
        return isValid;
    }
    private String codeOTP(EditText... views){
        StringBuilder builder = new StringBuilder();
        for (EditText v : views) {
            builder.append(v.getText().toString());
        }
        return builder.toString();
    }

    @OnClick(R.id.btn_verify_phone)
    public void btnVerifyClick() {
        setErrorNull(edt_one,edt_two,edt_three,edt_four,edt_five,edt_six);
        if (!validateOTP(edt_one,edt_two,edt_three,edt_four,edt_five,edt_six)) {
            return;
        }
        if (mVerificationId != null) {
            progressDialog.show();
            new AppUtilities().hideKeyBoard(this);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeOTP(edt_one,edt_two,edt_three,edt_four,edt_five,edt_six));
            signInWithPhoneAuthCredential(credential);
        } else {
            //Toast.makeText(this,"mVerificationId is null: ", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_login)
    public void action_Login() {
        if (validate()) {
            if (isReadAndReceiveSMSPermissionAllowed()) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    progressDialog.show();
                    progressDialog.setMessage("Generating OTP");
                    //@TODO @Chandra update the country code dynamic
                    countryCode = edt_phone_code.getText().toString();
                    startPhoneNumberVerification(countryCode + edt_phoneNo.getText().toString());

                    Answers.getInstance().logLogin(new LoginEvent()
                            .putMethod("Phone")
                            .putSuccess(true));
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            } else {
                requestReadAndReceiveSMSPermissionAllowed();
            }
        }
    }

    @Override
    public void profileAddressResponse(JsonUserAddressList jsonUserAddressList) {

    }

    @Override
    public void queueError() {
        dismissProgress();
    }

    @Override
    public void queueError(String error) {
        dismissProgress();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        //TODO(chandra)
    }
}
