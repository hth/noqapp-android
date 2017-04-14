package com.noqapp.client.views.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.noqapp.client.views.activities.NoQueueBaseActivity;
import com.noqapp.client.views.interfaces.MeView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;




import android.app.DatePickerDialog.OnDateSetListener;
import android.text.InputType;
import android.view.View.OnClickListener;



/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFormFragment extends NoQueueBaseFragment implements MeView,OnClickListener {

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

    private  static final String TAG = "RegistrationForm";
    public String gender = "";
    private DatePickerDialog fromDatePickerDialog;
    private String countryCode;
    private String countryISO;
    private SimpleDateFormat dateFormatter;
    private String country;

    public RegistrationFormFragment() {
        // Required empty public constructor
    }

    public void callRegistrationAPI()
    {
        String phoneNo = edt_phoneNo.getText().toString();
        String name = edt_Name.getText().toString();
        String mail = edt_Mail.getText().toString();
        String birthday = edt_birthday.getText().toString();
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID());

        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        //String ic = e

        Registration registrationbean = new Registration();
        registrationbean.setPhone(phoneNo);
        registrationbean.setFirstName(name);
        registrationbean.setMail(mail);
        registrationbean.setBirthday(birthday);
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
        ButterKnife.bind(this,view);
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

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        onClick(tv_male);

        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = tm.getSimCountryIso();


        Locale l = new Locale(Locale.getDefault().getLanguage(),countryCode);
        countryISO = l.getISO3Country();
                CountryPicker picker = CountryPicker.newInstance("Select Country");
        Country country = picker.getCountryByLocale(getActivity(),l);
        edt_country_code.setBackgroundResource(country.getFlag());
        edt_country_code.setError(null);
        edt_country_code.setText(country.getCode());
        this.country=country.getDialCode();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick(R.id.btnContinueRegistration)
    public void action_Registration(View view)
    {
        if(validate()) {


            final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.PHONE,
                            AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
            PhoneNumber pn =new PhoneNumber(country,edt_phoneNo.getText().toString(),countryISO);
            configurationBuilder.setInitialPhoneNumber(pn);


            // ... perform additional configuration ...
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, NoQueueBaseActivity.ACCOUNTKIT_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == NoQueueBaseActivity.ACCOUNTKIT_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("FB accont res:: ",data.toString());
                callRegistrationAPI();
            }
        }
    }

    @Override
    public void queueResponse(JsonProfile profile) {


        if(profile.getError() == null) {
            Log.d(TAG,"profile :"+profile.toString());
            SharedPreferences.Editor editor = ((NoQueueBaseActivity)getActivity()).getSharedprefEdit(getActivity());
            editor.putString(NoQueueBaseActivity.PREKEY_PHONE,profile.getPhoneRaw());
            editor.putString(NoQueueBaseActivity.PREKEY_NAME,profile.getName());
            editor.putString(NoQueueBaseActivity.PREKEY_GENDER,profile.getGender());
            editor.putString(NoQueueBaseActivity.PREKEY_MAIL,profile.getMail());
            editor.putInt(NoQueueBaseActivity.PREKEY_REMOTESCAN,profile.getRemoteScanAvailable());
            editor.putBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN,true);
            editor.putString(NoQueueBaseActivity.PREKEY_INVITECODE,profile.getInviteCode());
            editor.commit();
            replaceFragmentWithoutBackStack(getActivity(),R.id.frame_layout,new UserInfoFragment(),TAG);
        }else{
            //Rejected from  server
            ErrorEncounteredJson eej=profile.getError();
            if(null!=eej){
                ShowAlertInformation.showDialog(getActivity(),eej.getSystemError(),eej.getReason());
            }
        }
    }

    @Override
    public void queueError() {
        Log.d(TAG,"Error");
    }


    @Override
    public void onClick(View v) {
        if(v == edt_birthday) {
            fromDatePickerDialog.show();
        }else if(v == tv_male) {
            gender = "M";
            tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setBackgroundResource(R.drawable.square_redbg_drawable);
        }else if(v == tv_female) {
            gender = "F";
            tv_female.setBackgroundResource(R.drawable.square_redbg_drawable);
            tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
        }else if(v==edt_country_code){
            final CountryPicker picker = CountryPicker.newInstance("Select Country");
            picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
            picker.setListener(new CountryPickerListener() {

                @Override
                public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                    edt_country_code.setText(dialCode.toString());
                    country=dialCode.toString();
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

    private boolean validate(){
        boolean isValid=true;
        edt_phoneNo.setError(null);
        edt_Name.setError(null);
        edt_Mail.setError(null);
        edt_country_code.setError(null);
        if(TextUtils.isEmpty(edt_country_code.getText())){
            edt_country_code.setError("Please select country code");
            isValid=false;
        }
        if(TextUtils.isEmpty(edt_phoneNo.getText())){
            edt_phoneNo.setError("Please enter phone number");
            isValid=false;
        }

        if(TextUtils.isEmpty(edt_Name.getText())){
            edt_Name.setError("Please enter name");
            isValid=false;
        }
        if(TextUtils.isEmpty(edt_Name.getText())){
            edt_Name.setError("Please enter name");
            isValid=false;
        }
        if(!TextUtils.isEmpty(edt_Name.getText()) && edt_Name.getText().length()<4){
            edt_Name.setError("Name length should be greater than 3");
            isValid=false;
        }
        if(!TextUtils.isEmpty(edt_Mail.getText())&& !isValidEmail(edt_Mail.getText())) {
            edt_Mail.setError("Please enter valid email");
            isValid=false;
        }
            return isValid;
    }


}
