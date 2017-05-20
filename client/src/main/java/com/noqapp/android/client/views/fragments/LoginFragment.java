package com.noqapp.android.client.views.fragments;


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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;
import com.noqapp.android.client.model.RegisterModel;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.presenter.beans.ErrorEncounteredJson;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;

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
        CountryPicker picker = CountryPicker.newInstance(getString(R.string.select_country));
        Country country = picker.getCountryByLocale(getActivity(), l);
        edt_country_code.setBackgroundResource(country.getFlag());
        countryDialCode = country.getDialCode();

        edt_phoneNo.addTextChangedListener(tw);

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
                Log.d("FB Account data=", data.toString());
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    callLoginAPI();
                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
                    LaunchActivity.getLaunchActivity().dismissProgress();
                }
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
            final CountryPicker picker = CountryPicker.newInstance(getString(R.string.select_country));
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
        new AppUtilities().hideKeyBoard(getActivity());
        boolean isValid = true;
        edt_phoneNo.setError(null);
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError(getString(R.string.error_mobile_blank));
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
        ActivityCompat.requestPermissions(
                getActivity(),
                READ_AND_RECIEVE_SMS__PERMISSION_PERMS,
                READ_AND_RECIEVE_SMS__PERMISSION_CODE
        );
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
            SharedPreferences.Editor editor = ((NoQueueBaseActivity) getActivity()).getSharedPreferencesEditor(getActivity());
            editor.putString(NoQueueBaseActivity.PREKEY_PHONE, profile.getPhoneRaw());
            editor.putString(NoQueueBaseActivity.PREKEY_NAME, profile.getName());
            editor.putString(NoQueueBaseActivity.PREKEY_GENDER, profile.getGender());
            editor.putString(NoQueueBaseActivity.PREKEY_MAIL, profile.getMail());
            editor.putInt(NoQueueBaseActivity.PREKEY_REMOTESCAN, profile.getRemoteScanAvailable());
            editor.putBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN, true);
            editor.putString(NoQueueBaseActivity.PREKEY_INVITECODE, profile.getInviteCode());
            editor.putString(NoQueueBaseActivity.PREKEY_COUNTRY_SHORT_NAME, profile.getCountryShortName());
            editor.commit();
            replaceFragmentWithoutBackStack(getActivity(), R.id.frame_layout, new MeFragment(), TAG);
            LaunchActivity.getLaunchActivity().dismissProgress();
        } else {
            // Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (null != eej && eej.getSystemErrorCode().equals("412")) {
                Bundle b = new Bundle();
                b.putString("mobile_no", edt_phoneNo.getText().toString());
                b.putString("country_code", countryISO);
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

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(
                CharSequence s,
                int start,
                int count,
                int after) {
        }

        @Override
        public void onTextChanged(
                CharSequence s,
                int start,
                int before,
                int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.equals("")) {
                edt_phoneNo.removeTextChangedListener(tw);
                edt_phoneNo.setText(PhoneFormatterUtil.formatAsYouType(countryISO, s.toString()));
                edt_phoneNo.setSelection(edt_phoneNo.getText().length());//added to put the cursor at end
                edt_phoneNo.addTextChangedListener(tw);
            }
        }
    };
}
