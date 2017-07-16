package com.noqapp.android.client.views.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.RegisterModel;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.ErrorEncounteredJson;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends NoQueueBaseFragment implements ProfilePresenter, View.OnClickListener {
    private final String TAG = LoginFragment.class.getSimpleName();
    private final int READ_AND_RECEIVE_SMS_PERMISSION_CODE = 101;
    private final String[] READ_AND_RECEIVE_SMS_PERMISSION_PERMS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };
    private final int STATE_INITIALIZED = 1;
    private final int STATE_CODE_SENT = 2;
    private final int STATE_VERIFY_FAILED = 3;
    private final int STATE_VERIFY_SUCCESS = 4;
    private final int STATE_SIGN_IN_FAILED = 5;
    private final int STATE_SIGN_IN_SUCCESS = 6;
    @BindView(R.id.edt_phone)
    protected EditText edt_phoneNo;
    @BindView(R.id.btn_login)
    protected Button btn_login;
    @BindView(R.id.btn_verify_phone)
    protected Button btn_verify_phone;
    @BindView(R.id.edt_verification_code)
    protected EditText edt_verification_code;
    @BindView(R.id.tv_detail)
    protected TextView tv_detail;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private String verifiedMobileNo;
    private FirebaseAuth mAuth;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        RegisterModel.profilePresenter = this;
        mAuth = FirebaseAuth.getInstance();
        updateUI(STATE_INITIALIZED);
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
                LaunchActivity.getLaunchActivity().dismissProgress();
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                LaunchActivity.getLaunchActivity().dismissProgress();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                    // mPhoneNumberField.setError("Invalid phone number.");

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded

//                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                            Snackbar.LENGTH_SHORT).show();
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

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                // Update UI
                updateUI(STATE_CODE_SENT);

            }
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }

    @OnClick(R.id.btn_login)
    public void action_Login() {
        if (validate()) {
            if (isReadAndReceiveSMSPermissionAllowed()) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    startPhoneNumberVerification(edt_phoneNo.getText().toString());
                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                }
            } else {
                requestReadAndReceiveSMSPermissionAllowed();
            }
        }
    }


    private void callLoginAPI(String phoneNumber) {
        Login login = new Login();
        login.setPhone(phoneNumber);
        login.setCountryShortName("");
        RegisterModel.login(login);
    }


    private boolean validate() {
        new AppUtilities().hideKeyBoard(getActivity());
        boolean isValid = true;
        edt_phoneNo.setError(null);
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError(getString(R.string.error_mobile_blank));
            isValid = false;
        }

        return isValid;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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
                                edt_verification_code.setError("Invalid code.");
                            }
                            // Update UI
                            updateUI(STATE_SIGN_IN_FAILED);
                        }
                    }
                });
    }

    private boolean isReadAndReceiveSMSPermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS);
        int result_write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private void requestReadAndReceiveSMSPermissionAllowed() {
        ActivityCompat.requestPermissions(
                getActivity(),
                READ_AND_RECEIVE_SMS_PERMISSION_PERMS,
                READ_AND_RECEIVE_SMS_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == READ_AND_RECEIVE_SMS_PERMISSION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPhoneNumberVerification(edt_phoneNo.getText().toString());
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //No permission allowed
                //Do nothing
            }
        }
    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        if (profile.getError() == null) {
            Log.d(TAG, "profile :" + profile.toString());
            NoQueueBaseActivity.commitProfile(profile, email, auth);
            replaceFragmentWithoutBackStack(getActivity(), R.id.frame_layout, new MeFragment(), TAG);

            //remove the login fragment from stack
            List<Fragment> currentTabFragments = LaunchActivity.getLaunchActivity().fragmentsStack.get(LaunchActivity.tabMe);
            if (currentTabFragments.size() == 2) {
                LaunchActivity.getLaunchActivity().fragmentsStack.get(LaunchActivity.tabMe).remove(currentTabFragments.size() - 1);
            }
            LaunchActivity.getLaunchActivity().dismissProgress();
        } else {
            // Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (null != eej && eej.getSystemErrorCode().equals("412")) {
                Bundle b = new Bundle();
                b.putString("mobile_no", verifiedMobileNo);
                b.putString("country_code", "");
                RegistrationFragment rff = new RegistrationFragment();
                rff.setArguments(b);
                replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, rff, TAG, LaunchActivity.tabMe);
                LaunchActivity.getLaunchActivity().dismissProgress();
            }
        }
    }

    @Override
    public void queueError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


    private void enableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_phone:
                edt_verification_code.setError(null);
                String code = edt_verification_code.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    edt_verification_code.setError("Cannot be empty.");
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
                break;
        }
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(btn_login, edt_phoneNo);
                disableViews(btn_verify_phone, edt_verification_code);
                tv_detail.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(btn_login, edt_phoneNo, btn_verify_phone, edt_verification_code);

                tv_detail.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(btn_login, edt_phoneNo, btn_verify_phone, edt_verification_code);
                tv_detail.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                enableViews(edt_phoneNo, btn_verify_phone, edt_verification_code);
                disableViews(btn_login);
                tv_detail.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        edt_verification_code.setText(cred.getSmsCode());
                    } else {
                        edt_verification_code.setText(R.string.instant_validation);
                        disableViews(edt_verification_code);
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
                    callLoginAPI(user.getPhoneNumber());
                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                    LaunchActivity.getLaunchActivity().dismissProgress();
                }
                break;
        }
    }
}
