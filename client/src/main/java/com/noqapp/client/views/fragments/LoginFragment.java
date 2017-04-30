package com.noqapp.client.views.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;
import com.noqapp.client.R;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.model.RegisterModel;
import com.noqapp.client.presenter.ProfilePresenter;
import com.noqapp.client.presenter.beans.ErrorEncounteredJson;
import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.body.Login;
import com.noqapp.client.utils.AppUtilities;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.activities.NoQueueBaseActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends NoQueueBaseFragment implements ProfilePresenter, OnClickListener {
    private final String TAG = LoginFragment.class.getSimpleName();

    @BindView(R.id.edt_phone)
    EditText edt_phoneNo;

    @BindView(R.id.edt_country_code)
    EditText edt_country_code;

    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.iv_close)
    ImageView iv_close;

    private final int READ_AND_RECIEVE_SMS__PERMISSION_CODE = 101;
    private final String[] READ_AND_RECIEVE_SMS__PERMISSION_PERMS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };


    private String countryISO;
    private String countryDialCode;


    public LoginFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        edt_country_code.setOnClickListener(this);
        RegisterModel.profilePresenter = this;
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        if (StringUtils.isBlank(countryCode)) {
            countryCode = "US";
            Log.i(TAG, "Default country code=" + countryCode);
        }
        Locale l = new Locale(Locale.getDefault().getLanguage(), countryCode);
        countryISO = AppUtilities.iso3CountryCodeToIso2CountryCode(l.getISO3Country());
        CountryPicker picker = CountryPicker.newInstance("Select Country");
        Country country = picker.getCountryByLocale(getActivity(), l);
        edt_country_code.setBackgroundResource(country.getFlag());
        countryDialCode = country.getDialCode();
        iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick(R.id.btn_login)
    public void action_Login() {
        if (validate()) {
            if (isReadAndRecieveSMSPermissionAllowed()) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    LaunchActivity.getLaunchActivity().progressDialog.show();
                    callFacebookAccountKit();
                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                }
            } else {
                requestReadAndRecieveSMSPermissionAllowed();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NoQueueBaseActivity.ACCOUNTKIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("FB accont res:: ", data.toString());
                callLoginAPI();
            } else {
                LaunchActivity.getLaunchActivity().dismissProgress();
            }
        }
    }

    private void callLoginAPI() {

        Login login = new Login();
        login.setPhone(edt_phoneNo.getText().toString());
        login.setCountryShortName(countryISO);
        RegisterModel.login(login);

    }

    @Override
    public void onClick(View v) {
        if (v == edt_country_code) {
            final CountryPicker picker = CountryPicker.newInstance("Select Country");
            picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
            picker.setListener(new CountryPickerListener() {

                @Override
                public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                    edt_country_code.setText(dialCode.toString());
                    countryDialCode = dialCode.toString();
                    edt_country_code.setBackgroundResource(flagDrawableResID);
                    Locale l = new Locale(Locale.getDefault().getLanguage(), code);
                    countryISO = AppUtilities.iso3CountryCodeToIso2CountryCode(l.getISO3Country());
                    picker.dismiss();
                }


            });
        }
    }


    private boolean validate() {
        boolean isValid = true;
        edt_phoneNo.setError(null);
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError("Please enter phone number");
            isValid = false;
        }

        return isValid;
    }

    private void callFacebookAccountKit() {
        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        PhoneNumber pn = new PhoneNumber(countryDialCode, edt_phoneNo.getText().toString(), countryISO);
        configurationBuilder.setInitialPhoneNumber(pn);
        configurationBuilder.setReceiveSMS(true);

        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, NoQueueBaseActivity.ACCOUNTKIT_REQUEST_CODE);
    }

    private boolean isReadAndRecieveSMSPermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS);
        int result_write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private void requestReadAndRecieveSMSPermissionAllowed() {
        ActivityCompat.requestPermissions(getActivity(), READ_AND_RECIEVE_SMS__PERMISSION_PERMS,
                READ_AND_RECIEVE_SMS__PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == READ_AND_RECIEVE_SMS__PERMISSION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callFacebookAccountKit();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //No permission allowed
                //Do nothing
            }
        }
    }

    @Override
    public void queueResponse(JsonProfile profile) {
        if (profile.getError() == null) {
            Log.d(TAG, "profile :" + profile.toString());
            SharedPreferences.Editor editor = ((NoQueueBaseActivity) getActivity()).getSharedprefEdit(getActivity());
            editor.putString(NoQueueBaseActivity.PREKEY_PHONE, profile.getPhoneRaw());
            editor.putString(NoQueueBaseActivity.PREKEY_NAME, profile.getName());
            editor.putString(NoQueueBaseActivity.PREKEY_GENDER, profile.getGender());
            editor.putString(NoQueueBaseActivity.PREKEY_MAIL, profile.getMail());
            editor.putInt(NoQueueBaseActivity.PREKEY_REMOTESCAN, profile.getRemoteScanAvailable());
            editor.putBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN, true);
            editor.putString(NoQueueBaseActivity.PREKEY_INVITECODE, profile.getInviteCode());
            editor.commit();
            replaceFragmentWithoutBackStack(getActivity(), R.id.frame_layout, new MeFragment(), TAG);
            LaunchActivity.getLaunchActivity().dismissProgress();
        } else {
            // Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (null != eej && eej.getSystemErrorCode().equals("412")) {
                // pass mobile no and country code

                Bundle b = new Bundle();
                b.putString("mobile_no", edt_phoneNo.getText().toString());
                b.putString("country_code", countryISO);
                RegistrationFragment rff = new RegistrationFragment();
                rff.setArguments(b);
                replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, rff, TAG);
                LaunchActivity.getLaunchActivity().dismissProgress();
            }
        }

    }

    @Override
    public void queueError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }
}
