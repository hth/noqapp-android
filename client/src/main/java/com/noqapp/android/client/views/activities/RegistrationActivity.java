package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.BusinessTypeEnum;
import com.noqapp.android.client.presenter.MePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.ErrorEncounteredJson;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.client.views.fragments.MeFragment;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.client.views.fragments.RegistrationFragment;
import com.noqapp.android.client.views.interfaces.MeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationActivity extends AppCompatActivity implements MeView, View.OnClickListener {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;

    private final String TAG = RegistrationActivity.class.getSimpleName();
    public String gender = "";

    @BindView(R.id.edt_phone)
    protected EditText edt_phoneNo;

    @BindView(R.id.edt_name)
    protected EditText edt_Name;

    @BindView(R.id.edt_email)
    protected EditText edt_Mail;

    @BindView(R.id.edt_birthday)
    protected EditText edt_birthday;

    @BindView(R.id.tv_male)
    protected EditText tv_male;

    @BindView(R.id.tv_female)
    protected EditText tv_female;

    @BindView(R.id.ll_gender)
    protected LinearLayout ll_gender;

    @BindView(R.id.btnRegistration)
    protected Button btnRegistration;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Register");
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        edt_phoneNo.setEnabled(false);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);

                if (date_diff < 0) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.error_invalid_date), Toast.LENGTH_LONG).show();
                    edt_birthday.setText("");
                } else {
                    edt_birthday.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        onClick(tv_male);
        String phno = getIntent().getStringExtra("mobile_no");
        if (!TextUtils.isEmpty(phno)) {
            edt_phoneNo.setEnabled(false);
            edt_phoneNo.setText(phno);
        }
    }


    @OnClick(R.id.btnRegistration)
    public void action_Registration() {
        if (validate()) {
            btnRegistration.setBackgroundResource(R.drawable.button_drawable_red);
            btnRegistration.setTextColor(Color.WHITE);
            btnRegistration.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_white, 0);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                LaunchActivity.getLaunchActivity().progressDialog.show();
                callRegistrationAPI();
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        if (profile.getError() == null) {
            Log.d(TAG, "profile :" + profile.toString());
            NoQueueBaseActivity.commitProfile(profile, email, auth);
           /* replaceFragmentWithoutBackStack(this, R.id.frame_layout, new MeFragment(), TAG);
            //remove the login and register fragment from stack
            List<Fragment> currentTabFragments = LaunchActivity.getLaunchActivity().fragmentsStack.get(LaunchActivity.tabMe);
            if (currentTabFragments.size() == 3) {
                LaunchActivity.getLaunchActivity().fragmentsStack.get(LaunchActivity.tabMe).remove(currentTabFragments.size() - 1);
                LaunchActivity.getLaunchActivity().fragmentsStack.get(LaunchActivity.tabMe).remove(currentTabFragments.size() - 1);
            }*/
           finish();

        } else {
            //Rejected from  server
            ErrorEncounteredJson eej = profile.getError();
            if (null != eej) {
                ShowAlertInformation.showThemeDialog(this, eej.getSystemError(), eej.getReason());
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
            tv_female.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (v == tv_female) {
            gender = "F";
            tv_female.setBackgroundResource(R.drawable.gender_redbg);
            tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_female.setCompoundDrawablePadding(0);
            tv_male.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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
        btnRegistration.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        btnRegistration.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_small, 0);
        boolean isValid = true;
        edt_Name.setError(null);
        edt_Mail.setError(null);
        edt_birthday.setError(null);
        new AppUtilities().hideKeyBoard(this);

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
        registration.setBirthday(AppUtilities.convertDOBToValidFormat(birthday));
        registration.setGender(gender);
        registration.setTimeZoneId(tz.getID());
        registration.setCountryShortName("");
        registration.setInviteCode("");

        MePresenter mePresenter = new MePresenter(this);
        mePresenter.meView = this;
        mePresenter.callProfile(registration);
    }
}
