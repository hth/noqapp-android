package com.noqapp.client.views.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;
import com.noqapp.client.R;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.presenter.MePresenter;
import com.noqapp.client.presenter.beans.ErrorEncounteredJson;
import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.body.Registration;
import com.noqapp.client.utils.AppUtilities;
import com.noqapp.client.views.activities.NoQueueBaseActivity;
import com.noqapp.client.views.interfaces.MeView;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.noqapp.client.utils.AppUtilities.convertDOBToValidFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends NoQueueBaseFragment implements MeView, OnClickListener {

    @BindView(R.id.edt_phone)
    EditText edt_phoneNo;
    @BindView(R.id.edt_name)
    EditText edt_Name;
    @BindView(R.id.edt_email)
    EditText edt_Mail;
    @BindView(R.id.edt_birthday)
    EditText edt_birthday;


    @BindView(R.id.edt_country_code)
    EditText edt_country_code;
    @BindView(R.id.tv_male)
    EditText tv_male;
    @BindView(R.id.tv_female)
    EditText tv_female;
//color picker lib link -> https://github.com/madappstechnologies/country-picker-android

    private  final String TAG = "RegistrationForm";
    private final int READ_AND_RECIEVE_SMS__PERMISSION_CODE = 101;
    private  final String[] READ_AND_RECIEVE_SMS__PERMISSION_PERMS={
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };
    public String gender = "";
    private DatePickerDialog fromDatePickerDialog;
    private String countryCode;
    private String countryISO;
    private SimpleDateFormat dateFormatter;
    private String country;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public void callRegistrationAPI() {
        String phoneNo = edt_phoneNo.getText().toString();
        String name = edt_Name.getText().toString();
        String mail = edt_Mail.getText().toString();
        String birthday = edt_birthday.getText().toString();
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        //String ic = e

        Registration registrationbean = new Registration();
        registrationbean.setPhone(phoneNo);
        registrationbean.setFirstName(name);
        registrationbean.setMail(mail);
        registrationbean.setBirthday(convertDOBToValidFormat(birthday));
        registrationbean.setGender(gender);
        registrationbean.setTimeZoneId(tz.getID());
        registrationbean.setCountryShortName("IN"); // need to change
        registrationbean.setInviteCode("");

        MePresenter mePresenter = new MePresenter(getContext());
        mePresenter.meView = this;
        mePresenter.callProfile(registrationbean);

        if (accessToken != null) {
            //Handle Returning User
        } else {
            //Handle new or logged out user
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_form, container, false);
        ButterKnife.bind(this, view);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        edt_country_code.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edt_birthday.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        onClick(tv_male);

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = tm.getSimCountryIso();
        if (StringUtils.isBlank(countryCode)) {
            countryCode = "US";
            Log.i(TAG, "Default country code=" + countryCode);
        }
        Locale l = new Locale(Locale.getDefault().getLanguage(), countryCode);
        countryISO = AppUtilities.iso3CountryCodeToIso2CountryCode(l.getISO3Country());
        CountryPicker picker = CountryPicker.newInstance("Select Country");
        Country country = picker.getCountryByLocale(getActivity(), l);
        edt_country_code.setBackgroundResource(country.getFlag());
        edt_country_code.setError(null);
        edt_country_code.setText(country.getCode());
        this.country = country.getDialCode();
        Bundle bundle = getArguments();
        if(null!=bundle){
            edt_phoneNo.setText(bundle.getString("mobile_no",""));
            edt_phoneNo.setEnabled(false);

            //
            Locale l1 = new Locale(Locale.getDefault().getLanguage(), bundle.getString("country_code","US"));
            countryISO = AppUtilities.iso3CountryCodeToIso2CountryCode(l1.getISO3Country());
            CountryPicker picker1 = CountryPicker.newInstance("Select Country");
            Country country1 = picker1.getCountryByLocale(getActivity(), l1);
            edt_country_code.setBackgroundResource(country1.getFlag());
            edt_country_code.setError(null);
            edt_country_code.setText(country1.getCode());
            this.country = country1.getDialCode();
            edt_country_code.setOnClickListener(null);
        }else {

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick(R.id.btnContinueRegistration)
    public void action_Registration(View view) {
        if (validate()) {
            if(isReadAndRecieveSMSPermissionAllowed()) {
                callFacebookAccountKit();
            }else{
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
                callRegistrationAPI();
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
        } else {
            //Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (null != eej) {
                ShowAlertInformation.showDialog(getActivity(), eej.getSystemError(), eej.getReason());
            }
        }
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Error");
    }


    @Override
    public void onClick(View v) {
        if (v == edt_birthday) {
            fromDatePickerDialog.show();
        } else if (v == tv_male) {
            gender = "M";
            tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setBackgroundResource(R.drawable.square_redbg_drawable);
        } else if (v == tv_female) {
            gender = "F";
            tv_female.setBackgroundResource(R.drawable.square_redbg_drawable);
            tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
        } else if (v == edt_country_code) {
            final CountryPicker picker = CountryPicker.newInstance("Select Country");
            picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
            picker.setListener(new CountryPickerListener() {

                @Override
                public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                    edt_country_code.setText(dialCode.toString());
                    country = dialCode.toString();
                    edt_country_code.setError(null);
                    edt_country_code.setBackgroundResource(flagDrawableResID);
                    picker.dismiss();
                }


            });
        }
    }

    private boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private boolean validate() {
        boolean isValid = true;
        edt_phoneNo.setError(null);
        edt_Name.setError(null);
        edt_Mail.setError(null);
        edt_country_code.setError(null);
        if (TextUtils.isEmpty(edt_country_code.getText())) {
            edt_country_code.setError("Please select country code");
            isValid = false;
        }
        if (TextUtils.isEmpty(edt_phoneNo.getText())) {
            edt_phoneNo.setError("Please enter phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(edt_Name.getText())) {
            edt_Name.setError("Please enter name");
            isValid = false;
        }
        if (TextUtils.isEmpty(edt_Name.getText())) {
            edt_Name.setError("Please enter name");
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Name.getText()) && edt_Name.getText().length() < 4) {
            edt_Name.setError("Name length should be greater than 3");
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Mail.getText()) && !isValidEmail(edt_Mail.getText())) {
            edt_Mail.setError("Please enter valid email");
            isValid = false;
        }
        return isValid;
    }

    private void callFacebookAccountKit(){
        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        PhoneNumber pn = new PhoneNumber(country, edt_phoneNo.getText().toString(), countryISO);
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
        if (result_read == PackageManager.PERMISSION_GRANTED &&result_write == PackageManager.PERMISSION_GRANTED )
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

          if ( grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callFacebookAccountKit();
            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                //No permission allowed
                //Do nothing
            }
        }
    }
}
