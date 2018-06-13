package com.noqapp.android.merchant.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;
import com.noqapp.common.beans.JsonProfile;
import com.noqapp.common.beans.body.UpdateProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class UserProfileFragment extends Fragment implements View.OnClickListener , ProfilePresenter {

    private EditText edt_birthday;
    private EditText edt_address;
    private Button btn_update;
    private EditText edt_phoneNo;
    private EditText edt_name;
    private EditText edt_email;
    private EditText tv_male;
    private EditText tv_female;
    public String gender = "";
    protected LinearLayout ll_gender;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        tv_male = view.findViewById(R.id.tv_male);
        tv_female = view.findViewById(R.id.tv_female);

        edt_email = view.findViewById(R.id.edt_email);
        edt_name = view.findViewById(R.id.edt_name);
        edt_phoneNo = view.findViewById(R.id.edt_phone);

        btn_update = view.findViewById(R.id.btn_update);
        edt_address = view.findViewById(R.id.edt_address);
        edt_birthday = view.findViewById(R.id.edt_birthday);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        edt_birthday.setInputType(InputType.TYPE_NULL);
        btn_update = view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);

                if (date_diff < 0) {
                    Toast.makeText(getActivity(), getString(R.string.error_invalid_date), Toast.LENGTH_LONG).show();
                    edt_birthday.setText("");
                } else {
                    edt_birthday.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            // progressDialog.show();
            // ProfileModel.profilePresenter = this;
            // ProfileModel.fetchProfile(UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.edt_birthday:
                fromDatePickerDialog.show();
                break;
                case R.id.btn_update:
                updateProfile();
                break;
            case R.id.tv_male:
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
                tv_female.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
            case R.id.tv_female:
                gender = "F";
                tv_female.setBackgroundResource(R.drawable.gender_redbg);
                tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_female.setCompoundDrawablePadding(0);
                tv_male.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tv_male.setTextColor(Color.BLACK);
                tv_female.setTextColor(Color.WHITE);
                SpannableString ss1 = new SpannableString("Female  ");
                Drawable d1 = getResources().getDrawable(R.drawable.check_white);
                d1.setBounds(0, 0, d1.getIntrinsicWidth(), d1.getIntrinsicHeight());
                ImageSpan span1 = new ImageSpan(d1, ImageSpan.ALIGN_BASELINE);
                ss1.setSpan(span1, 7, 8, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_female.setText(ss1);
                break;


        }
    }

    public void updateProfile() {

        if (validate()) {
            btn_update.setBackgroundResource(R.drawable.button_drawable_red);
            btn_update.setTextColor(Color.WHITE);
            btn_update.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_white, 0);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
             //   progressDialog.show();
                MerchantProfileModel.profilePresenter = this;
                //   String phoneNo = edt_phoneNo.getText().toString();
                String name = edt_name.getText().toString();
                //   String mail = edt_Mail.getText().toString();
                String birthday = edt_birthday.getText().toString();
                String address = edt_address.getText().toString();
                UpdateProfile updateProfile = new UpdateProfile();
                updateProfile.setAddress(address);
                updateProfile.setFirstName(name);
                updateProfile.setBirthday(convertDOBToValidFormat(birthday));
                updateProfile.setGender(gender);
                updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                MerchantProfileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
    }
    private static final SimpleDateFormat SDF_DOB_FROM_UI = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat SDF_DOB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = SDF_DOB_FROM_UI.parse(dob);
            return SDF_DOB.format(date);
        } catch (ParseException e) {
            Log.e(UserProfileFragment.class.getSimpleName(), "Error parsing DOB={}" + e.getLocalizedMessage(), e);
            return "";
        }
    }
    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
       // NoQueueBaseActivity.commitProfile(profile, email, auth);
       // dismissProgress();
        //updateUI();
    }




    @Override
    public void profileError() {
       // dismissProgress();
    }



    @Override
    public void authenticationFailure(int errorCode) {
        //TODO(chandra)
    }

    public void updateUI(JsonProfile jsonProfile) {
        edt_name.setText(jsonProfile.getName());
        edt_phoneNo.setText(jsonProfile.getPhoneRaw());
        edt_email.setText(jsonProfile.getMail());
        edt_phoneNo.setEnabled(false);
        edt_email.setEnabled(false);
        edt_address.setText(jsonProfile.getAddress());
        if (jsonProfile.getGender().equals("M")) {
            onClick(tv_male);
        } else {
            onClick(tv_female);
        }
        try {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = dateFormatter.format(fromUser.parse(jsonProfile.getBirthday()));
            edt_birthday.setText(reformattedStr);
        } catch (Exception e) {
            e.printStackTrace();
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
        btn_update.setBackgroundResource(R.drawable.button_drawable);
        btn_update.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
        btn_update.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_small, 0);
        boolean isValid = true;
        edt_name.setError(null);
        edt_email.setError(null);
        edt_birthday.setError(null);
        new AppUtils().hideKeyBoard(getActivity());
        if (TextUtils.isEmpty(edt_name.getText())) {
            edt_name.setError(getString(R.string.error_name_blank));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_name.getText()) && edt_name.getText().length() < 4) {
            edt_name.setError(getString(R.string.error_name_length));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_email.getText()) && !isValidEmail(edt_email.getText())) {
            edt_email.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        if (TextUtils.isEmpty(edt_birthday.getText())) {
            edt_birthday.setError(getString(R.string.error_dob_blank));
            isValid = false;
        }
        return isValid;
    }

}

