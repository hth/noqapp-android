package com.noqapp.client.views.fragments;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.models.Country;
import com.noqapp.client.R;
import com.noqapp.client.helper.PhoneFormatterUtil;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.presenter.MePresenter;
import com.noqapp.client.presenter.beans.ErrorEncounteredJson;
import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.body.Registration;
import com.noqapp.client.utils.AppUtilities;
import com.noqapp.client.views.activities.LaunchActivity;
import com.noqapp.client.views.activities.NoQueueBaseActivity;
import com.noqapp.client.views.interfaces.MeView;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private final String TAG = RegistrationFragment.class.getSimpleName();

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
    @BindView(R.id.ll_gender)
    LinearLayout ll_gender;
    @BindView(R.id.btnRegistration)
    Button btnRegistration;

//color picker lib link -> https://github.com/madappstechnologies/country-picker-android

    public String gender = "";
    private DatePickerDialog fromDatePickerDialog;
    private String countryDialCode;
    private String countryISO;
    private SimpleDateFormat dateFormatter;


    public RegistrationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, view);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        edt_phoneNo.setEnabled(false);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff =new Date().compareTo(current);

                if(date_diff<0){
                    Toast.makeText(getActivity(), "Please select a valid date",  Toast.LENGTH_LONG).show();
                    edt_birthday.setText("");
                }
                else{
                    edt_birthday.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        onClick(tv_male);
        Bundle bundle = getArguments();
        if (null != bundle) {
            // edt_phoneNo.setText(bundle.getString("mobile_no", ""));
            edt_phoneNo.setEnabled(false);
            Locale l1 = new Locale(Locale.getDefault().getLanguage(), bundle.getString("country_code", "US"));
            countryISO = AppUtilities.iso3CountryCodeToIso2CountryCode(l1.getISO3Country());
            CountryPicker picker1 = CountryPicker.newInstance("Select Country");
            Country country1 = picker1.getCountryByLocale(getActivity(), l1);
            edt_country_code.setBackgroundResource(country1.getFlag());
            edt_country_code.setError(null);
            edt_country_code.setText(country1.getCode());
            countryDialCode = country1.getDialCode();
            edt_country_code.setOnClickListener(null);
            edt_phoneNo.setText(PhoneFormatterUtil.formatAsYouType(countryISO, bundle.getString("mobile_no", "0000000000")));
        } else {
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
            edt_country_code.setText(country.getCode());
            countryDialCode = country.getDialCode();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().enableDisableBack(true);

    }

    @OnClick(R.id.btnRegistration)
    public void action_Registration() {
        if (validate()) {
            btnRegistration.setBackgroundResource(R.drawable.button_drawable_red);
            btnRegistration.setTextColor(Color.WHITE);
            btnRegistration.setCompoundDrawablesWithIntrinsicBounds(
                    0,0,R.drawable.arrow_white,0);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                LaunchActivity.getLaunchActivity().progressDialog.show();
                callRegistrationAPI();
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
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
            //remove the login and register fragment from stack
            LaunchActivity.getLaunchActivity().fragmentsStack.get(LaunchActivity.tabMe).clear();
        } else {
            //Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (null != eej) {
                ShowAlertInformation.showDialog(getActivity(), eej.getSystemError(), eej.getReason());
            }
        }
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


    @Override
    public void onClick(View v) {
        if (v == edt_birthday) {
            fromDatePickerDialog.show();
        } else if (v == tv_male) {
            gender = "M";
            tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_male.setBackgroundResource(R.drawable.gender_redbg);

            SpannableString ss = new SpannableString("Male  ");
            Drawable d = getResources().getDrawable(R.drawable.check_white);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, 5, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_male.setText(ss);
            tv_male.setTextColor(Color.WHITE);
            tv_female.setTextColor(Color.BLACK);
            tv_female.setCompoundDrawablesWithIntrinsicBounds(
                    0,0,0,0);
        } else if (v == tv_female) {
            gender = "F";
            tv_female.setBackgroundResource(R.drawable.gender_redbg);
            tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_female.setCompoundDrawablePadding(0);
            tv_male.setCompoundDrawablesWithIntrinsicBounds(
                    0,0,0,0);
            tv_male.setTextColor(Color.BLACK);
            tv_female.setTextColor(Color.WHITE);
            SpannableString ss = new SpannableString("Female  ");
            Drawable d = getResources().getDrawable(R.drawable.check_white);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, 7, 8, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_female.setText(ss);
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
        btnRegistration.setBackgroundResource(R.drawable.button_drawable);
        btnRegistration.setTextColor(getResources().getColor(R.color.colorMobile));
        btnRegistration.setCompoundDrawablesWithIntrinsicBounds(
                0,0,R.drawable.arrow_small,0);
        boolean isValid = true;
        edt_Name.setError(null);
        edt_Mail.setError(null);
        new AppUtilities().hideKeyBoard(getActivity());

        if (TextUtils.isEmpty(edt_Name.getText())) {
            edt_Name.setError("Please enter name");
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Name.getText()) && edt_Name.getText().length() < 5) {
            edt_Name.setError("Name length should be greater than 3");
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Mail.getText()) && !isValidEmail(edt_Mail.getText())) {
            edt_Mail.setError("Please enter valid email");
            isValid = false;
        }
        return isValid;
    }

    public void callRegistrationAPI() {
        String phoneNo = edt_phoneNo.getText().toString();
        String name = edt_Name.getText().toString();
        String mail = edt_Mail.getText().toString();
        String birthday = edt_birthday.getText().toString();
        TimeZone tz = TimeZone.getDefault();
        Log.d(TAG, "TimeZone=" + tz.getDisplayName(false, TimeZone.SHORT) + " TimezoneId=" + tz.getID());

        Registration registration = new Registration();
        registration.setPhone(phoneNo);
        registration.setFirstName(name);
        registration.setMail(mail);
        registration.setBirthday(convertDOBToValidFormat(birthday));
        registration.setGender(gender);
        registration.setTimeZoneId(tz.getID());
        registration.setCountryShortName(countryISO);
        registration.setInviteCode("");

        MePresenter mePresenter = new MePresenter(getContext());
        mePresenter.meView = this;
        mePresenter.callProfile(registration);
    }

}
