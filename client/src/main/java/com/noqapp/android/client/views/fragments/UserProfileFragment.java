package com.noqapp.android.client.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.presenter.beans.body.UpdateProfile;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.MigrateActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.activities.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UserProfileFragment extends Fragment implements View.OnClickListener, ProfilePresenter {

    @BindView(R.id.edt_birthday)
    protected EditText edt_birthday;
    @BindView(R.id.edt_address)
    protected EditText edt_address;
    @BindView(R.id.btn_update)
    protected Button btn_update;
    @BindView(R.id.edt_phone)
    protected EditText edt_phoneNo;
    @BindView(R.id.edt_name)
    protected EditText edt_Name;
    @BindView(R.id.edt_email)
    protected EditText edt_Mail;


    @BindView(R.id.tv_male)
    protected EditText tv_male;
    @BindView(R.id.tv_female)
    protected EditText tv_female;
    @BindView(R.id.tv_migrate)
    protected TextView tv_migrate;
    @BindView(R.id.ll_gender)
    protected LinearLayout ll_gender;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    public String gender = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        updateUI();
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_migrate.setOnClickListener(this);
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

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            //progressDialog.show();
            ProfileModel.profilePresenter = this;
            ProfileModel.fetchProfile(UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.edt_birthday:
                fromDatePickerDialog.show();

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

            case R.id.tv_migrate:
                Intent migrate = new Intent(getActivity(),MigrateActivity.class);
                startActivity(migrate);
                break;
        }
    }



    @OnClick(R.id.btn_update)
    public void updateProfile() {

        if (validate()) {
            btn_update.setBackgroundResource(R.drawable.button_drawable_red);
            btn_update.setTextColor(Color.WHITE);
            btn_update.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_white, 0);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
               // progressDialog.show();
                ProfileModel.profilePresenter = this;
                //   String phoneNo = edt_phoneNo.getText().toString();
                String name = edt_Name.getText().toString();
                //   String mail = edt_Mail.getText().toString();
                String birthday = edt_birthday.getText().toString();
                String address = edt_address.getText().toString();
                UpdateProfile updateProfile = new UpdateProfile();
                updateProfile.setAddress(address);
                updateProfile.setFirstName(name);
                updateProfile.setBirthday(AppUtilities.convertDOBToValidFormat(birthday));
                updateProfile.setGender(gender);
                updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                ProfileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }

    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        NoQueueBaseActivity.commitProfile(profile, email, auth);
        if(!TextUtils.isEmpty(profile.getProfileImage()))
            Picasso.with(getActivity())
                    .load(BuildConfig.AWSS3+BuildConfig.PROFILE_BUCKET+profile.getProfileImage())
                    .into(UserProfileActivity.iv_profile);
        else{
            Picasso.with(getActivity()).load(R.drawable.profile_avatar).into(UserProfileActivity.iv_profile);
        }
       // dismissProgress();
        updateUI();
    }

    @Override
    public void profileAddressResponse(JsonUserAddressList jsonUserAddressList) {

    }

    @Override
    public void queueError() {
        //dismissProgress();
    }

    @Override
    public void queueError(String error) {

    }

    @Override
    public void authenticationFailure(int errorCode) {
        //TODO(chandra)
    }

    private void updateUI() {
        edt_Name.setText(NoQueueBaseActivity.getUserName());
        edt_phoneNo.setText(NoQueueBaseActivity.getPhoneNo());
        edt_Mail.setText(NoQueueBaseActivity.getMail().contains("noqapp.com")?"":NoQueueBaseActivity.getMail());
        edt_phoneNo.setEnabled(false);
        edt_Mail.setEnabled(false);
        edt_address.setText(NoQueueBaseActivity.getAddress());
        if(NoQueueBaseActivity.getGender().equals("M")){
            onClick(tv_male);
        }else{
            onClick(tv_female);
        }
        // edt_address.setText(NoQueueBaseActivity.geta);
        try {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = dateFormatter.format(fromUser.parse(NoQueueBaseActivity.getUserDOB()));
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
        edt_Name.setError(null);
        edt_Mail.setError(null);
        edt_birthday.setError(null);
        new AppUtilities().hideKeyBoard(getActivity());

        if (TextUtils.isEmpty(edt_Name.getText())) {
            edt_Name.setError(getString(R.string.error_name_blank));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Name.getText()) && edt_Name.getText().length() < 4) {
            edt_Name.setError(getString(R.string.error_name_length));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Mail.getText()) && !isValidEmail(edt_Mail.getText())) {
            edt_Mail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        if (TextUtils.isEmpty(edt_birthday.getText())) {
            edt_birthday.setError(getString(R.string.error_dob_blank));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

}
