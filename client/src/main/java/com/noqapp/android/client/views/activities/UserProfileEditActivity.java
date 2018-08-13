package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 10/4/18.
 */

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DependencyModel;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.DependencyPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.ImagePathReader;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.squareup.picasso.Picasso;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class UserProfileEditActivity extends ProfileActivity implements View.OnClickListener, ImageUploadPresenter, ProfilePresenter, DependencyPresenter {

    private ImageView iv_profile;
    public String gender = "";
    @BindView(R.id.tv_name)
    protected TextView tv_name;
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
    @BindView(R.id.ll_gender)
    protected LinearLayout ll_gender;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private boolean isDependent = false;
    private JsonProfile dependentProfile = null;
    private ProfileModel profileModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);
        ButterKnife.bind(this);
        initActionsViews(false);
        tv_toolbar_title.setText("Edit Profile");
        iv_profile = findViewById(R.id.iv_profile);
        profileModel = new ProfileModel();
        loadProfilePic();
        iv_profile.setOnClickListener(this);
        isDependent = getIntent().getBooleanExtra(NoQueueBaseActivity.IS_DEPENDENT, false);
        dependentProfile = (JsonProfile) getIntent().getSerializableExtra(NoQueueBaseActivity.DEPENDENT_PROFILE);
        // gaurdianProfile = (JsonProfile) getIntent().getSerializableExtra(NoQueueBaseActivity.KEY_USER_PROFILE);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        updateUI();
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        edt_address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date current = newDate.getTime();
                int date_diff = new Date().compareTo(current);

                if (date_diff < 0) {
                    Toast.makeText(UserProfileEditActivity.this, getString(R.string.error_invalid_date), Toast.LENGTH_LONG).show();
                    edt_birthday.setText("");
                } else {
                    edt_birthday.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void loadProfilePic() {
        Picasso.with(this).load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(NoQueueBaseActivity.getUserProfileUri())) {
                Picasso.with(this)
                        .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, NoQueueBaseActivity.getUserProfileUri()))
                        .placeholder(ImageUtils.getProfilePlaceholder(this))
                        .error(ImageUtils.getProfileErrorPlaceholder(this))
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void imageUploadResponse(JsonResponse jsonResponse) {
        Log.v("Image upload", "" + jsonResponse.getResponse());
    }

    @Override
    public void imageUploadError() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_profile:
                selectImage();
                break;

            case R.id.edt_birthday:
                fromDatePickerDialog.show();
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    iv_profile.setImageBitmap(bitmap);

                    String convertedPath = new ImagePathReader().getPathFromUri(this, selectedImage);
                    NoQueueBaseActivity.setUserProfileUri(convertedPath);

                    if (!TextUtils.isEmpty(convertedPath)) {
                        String type = getMimeType(this, selectedImage);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody profileImageOfQid = RequestBody.create(MediaType.parse("text/plain"), LaunchActivity.getLaunchActivity().getUserProfile().getQueueUserId());
                        profileModel.setImageUploadPresenter(this);
                        profileModel.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, profileImageOfQid);
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
                progressDialog.show();
                profileModel.setProfilePresenter(this);
                //   String phoneNo = edt_phoneNo.getText().toString();
                String name = edt_Name.getText().toString();
                //   String mail = edt_Mail.getText().toString();
                String birthday = edt_birthday.getText().toString();
                String address = edt_address.getText().toString();
                if (isDependent) {
                    if (null != dependentProfile) {
                        UpdateProfile updateProfile = new UpdateProfile();
                        updateProfile.setAddress(address);
                        updateProfile.setFirstName(name);
                        updateProfile.setBirthday(AppUtilities.convertDOBToValidFormat(birthday));
                        updateProfile.setGender(gender);
                        updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                        updateProfile.setQueueUserId(dependentProfile.getQueueUserId());
                        profileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                    }else {
                        Registration registration = new Registration();
                        registration.setPhone(PhoneFormatterUtil.phoneNumberWithCountryCode(NoQueueBaseActivity.getPhoneNo(), NoQueueBaseActivity.getCountryShortName()));
                        registration.setFirstName(name);
                        registration.setMail("");
                        registration.setPassword("");
                        registration.setBirthday(AppUtilities.convertDOBToValidFormat(birthday));
                        registration.setGender(gender);
                        registration.setTimeZoneId(TimeZone.getDefault().getID());
                        registration.setCountryShortName(NoQueueBaseActivity.getCountryShortName());
                        registration.setInviteCode("");
                        DependencyModel dependencyModel = new DependencyModel(this);
                        dependencyModel.addDependency(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), registration);
                    }
                }else {
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setAddress(address);
                    updateProfile.setFirstName(name);
                    updateProfile.setBirthday(AppUtilities.convertDOBToValidFormat(birthday));
                    updateProfile.setGender(gender);
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    updateProfile.setQueueUserId(LaunchActivity.getLaunchActivity().getUserProfile().getQueueUserId());
                    profileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }

    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
        dismissProgress();
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void queueError() {
        dismissProgress();
    }

    @Override
    public void queueError(String error) {
        dismissProgress();
    }

    @Override
    public void dependencyResponse(JsonProfile profile) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.setUserProfile(profile);
        dependentProfile = profile;
        dismissProgress();
        Toast.makeText(this, "Dependent added successfully", Toast.LENGTH_LONG).show();
        // updateUI();
        finish();
    }

    @Override
    public void dependencyError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        //TODO(chandra)
    }

    private void updateUI() {
        if (isDependent) {
            onClick(tv_male);// set default
            if (null != dependentProfile) {
                edt_Name.setText(dependentProfile.getName());
                tv_name.setText(dependentProfile.getName());
                edt_address.setText(dependentProfile.getAddress());
//                if (dependentProfile.getGender().name().equals("M")) {
//                    onClick(tv_male);
//                } else {
//                    onClick(tv_female);
//                }
                try {
                    SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
                    String reformattedStr = dateFormatter.format(fromUser.parse(dependentProfile.getBirthday()));
                    edt_birthday.setText(reformattedStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                btn_update.setText("Add Dependent");
            }
        } else {
            edt_Name.setText(NoQueueBaseActivity.getUserName());
            tv_name.setText(NoQueueBaseActivity.getUserName());
            edt_address.setText(NoQueueBaseActivity.getAddress());
            if (NoQueueBaseActivity.getGender().equals("M")) {
                onClick(tv_male);
            } else {
                onClick(tv_female);
            }
            try {
                SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
                String reformattedStr = dateFormatter.format(fromUser.parse(NoQueueBaseActivity.getUserDOB()));
                edt_birthday.setText(reformattedStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        edt_phoneNo.setText(NoQueueBaseActivity.getPhoneNo());
        edt_Mail.setText(NoQueueBaseActivity.getActualMail());
        edt_phoneNo.setEnabled(false);
        edt_Mail.setEnabled(false);

        loadProfilePic();
    }

    private boolean validate() {
        btn_update.setBackgroundResource(R.drawable.button_drawable);
        btn_update.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        btn_update.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_small, 0);
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

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}

