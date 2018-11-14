package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DependencyModel;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.DependencyPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.CommonHelper;
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
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class UserProfileEditActivity extends ProfileActivity implements View.OnClickListener, ImageUploadPresenter, ProfilePresenter, DependencyPresenter {

    private ImageView iv_profile;
    private String gender = "";
    private TextView tv_name;
    private TextView tv_birthday;
    private EditText edt_address;
    private Button btn_update;
    private EditText edt_phoneNo;
    private EditText edt_Name;
    private EditText edt_Mail;
    private TextView tv_male;
    private TextView tv_female;
    private TextView tv_remove_image;

    private DatePickerDialog fromDatePickerDialog;
    private boolean isDependent = false;
    private JsonProfile dependentProfile = null;
    private ProfileModel profileModel;
    private List<String> nameList = new ArrayList<>();
    private String imageUrl = "";
    private String qUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        tv_name = findViewById(R.id.tv_name);
        tv_birthday = findViewById(R.id.tv_birthday);
        edt_address = findViewById(R.id.edt_address);
        btn_update = findViewById(R.id.btn_update);
        edt_phoneNo = findViewById(R.id.edt_phone);
        edt_Name = findViewById(R.id.edt_name);
        edt_Mail = findViewById(R.id.edt_email);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);
        tv_remove_image = findViewById(R.id.tv_remove_image);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_edit_profile));
        iv_profile = findViewById(R.id.iv_profile);
        profileModel = new ProfileModel();
        iv_profile.setOnClickListener(this);
        progressDialog.setMessage("Updating profile....");
        isDependent = getIntent().getBooleanExtra(NoQueueBaseActivity.IS_DEPENDENT, false);
        dependentProfile = (JsonProfile) getIntent().getSerializableExtra(NoQueueBaseActivity.DEPENDENT_PROFILE);
        // gaurdianProfile = (JsonProfile) getIntent().getSerializableExtra(NoQueueBaseActivity.KEY_USER_PROFILE);
        nameList = getIntent().getStringArrayListExtra("nameList");

        updateUI();
        tv_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        tv_remove_image.setOnClickListener(this);
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
                    tv_birthday.setText("");
                } else {
                    tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void loadProfilePic() {
        Picasso.with(this).load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(this)
                        .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, imageUrl))
                        .placeholder(ImageUtils.getProfilePlaceholder(this))
                        .error(ImageUtils.getProfileErrorPlaceholder(this))
                        .into(iv_profile);
                tv_remove_image.setVisibility(View.VISIBLE);
            } else {
                tv_remove_image.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_remove_image.setVisibility(View.GONE);
        }
    }

    @Override
    public void imageUploadResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image upload", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Profile image change successfully! Change will be reflect after 5 min", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Picasso.with(this).load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
            tv_remove_image.setVisibility(View.GONE);
            if (isDependent) {
                setDependentProfileImageUrl("");
            } else {
                NoQueueBaseActivity.setUserProfileUri("");
            }
            Toast.makeText(this, "Profile image removed successfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to remove profile image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void imageUploadError() {
        dismissProgress();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_remove_image: {
                progressDialog.show();
                progressDialog.setMessage("Removing profile image");
                profileModel.setImageUploadPresenter(this);
                profileModel.removeImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new UpdateProfile().setQueueUserId(qUserId));
            }
            break;
            case R.id.iv_profile:
                selectImage();
                break;
            case R.id.btn_update:
                updateProfile();
                break;

            case R.id.tv_birthday:
                fromDatePickerDialog.show();
                break;
            case R.id.tv_male:
                gender = "M";
                tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setBackgroundColor(ContextCompat.getColor(UserProfileEditActivity.this, R.color.review_color));
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
                tv_female.setBackgroundColor(ContextCompat.getColor(UserProfileEditActivity.this, R.color.review_color));
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
                    if (isDependent) {
                        setDependentProfileImageUrl(convertedPath);
                    } else {
                        NoQueueBaseActivity.setUserProfileUri(convertedPath);
                    }

                    if (!TextUtils.isEmpty(convertedPath)) {
                        progressDialog.show();
                        progressDialog.setMessage("Updating profile image");
                        String type = getMimeType(this, selectedImage);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody profileImageOfQid = RequestBody.create(MediaType.parse("text/plain"), qUserId);
                        profileModel.setImageUploadPresenter(this);
                        profileModel.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, profileImageOfQid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void updateProfile() {

        if (validate()) {
            btn_update.setBackgroundResource(R.drawable.blue_gradient_or);
            btn_update.setTextColor(Color.WHITE);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                profileModel.setProfilePresenter(this);
                //   String phoneNo = edt_phoneNo.getText().toString();
                String name = edt_Name.getText().toString();
                //   String mail = edt_Mail.getText().toString();
                String birthday = tv_birthday.getText().toString();
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
                    } else {
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
                } else {
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setAddress(address);
                    updateProfile.setFirstName(name);
                    updateProfile.setBirthday(AppUtilities.convertDOBToValidFormat(birthday));
                    updateProfile.setGender(gender);
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    updateProfile.setQueueUserId(NoQueueBaseActivity.getUserProfile().getQueueUserId());
                    profileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }

    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
        dismissProgress();
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void profileError() {
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
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }


    private void updateUI() {
        if (isDependent) {
            onClick(tv_male);// set default
            if (null != dependentProfile) {
                edt_Name.setText(dependentProfile.getName());
                tv_name.setText(dependentProfile.getName());
                edt_address.setText(dependentProfile.getAddress());
                imageUrl = dependentProfile.getProfileImage();
                qUserId = dependentProfile.getQueueUserId();
                if (dependentProfile.getGender().name().equals("M")) {
                    onClick(tv_male);
                } else {
                    onClick(tv_female);
                }
                try {
                    tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(dependentProfile.getBirthday())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                btn_update.setText("Add Family Members");
                progressDialog.setMessage("Adding family member....");
                tv_toolbar_title.setText("Add Profile");
                iv_profile.setEnabled(false);
                iv_profile.setClickable(false);
                imageUrl = "";
                qUserId = "";
            }
        } else {
            edt_Name.setText(NoQueueBaseActivity.getUserName());
            tv_name.setText(NoQueueBaseActivity.getUserName());
            edt_address.setText(NoQueueBaseActivity.getAddress());
            imageUrl = NoQueueBaseActivity.getUserProfileUri();
            qUserId = NoQueueBaseActivity.getUserProfile().getQueueUserId();
            if (NoQueueBaseActivity.getGender().equals("M")) {
                onClick(tv_male);
            } else {
                onClick(tv_female);
            }
            try {
                tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(NoQueueBaseActivity.getUserDOB())));
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
        btn_update.setBackgroundResource(R.drawable.grey_gradient);
        btn_update.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        boolean isValid = true;
        edt_Name.setError(null);
        edt_Mail.setError(null);
        tv_birthday.setError(null);
        new AppUtilities().hideKeyBoard(this);
        String name = edt_Name.getText().toString().toUpperCase();
        if (TextUtils.isEmpty(name)) {
            edt_Name.setError(getString(R.string.error_name_blank));
            isValid = false;
        }
        if (!TextUtils.isEmpty(name) && name.length() < 4) {
            edt_Name.setError(getString(R.string.error_name_length));
            isValid = false;
        }
        if (((null == dependentProfile && isDependent && nameList.contains(name))) || (null == dependentProfile && !isDependent && !NoQueueBaseActivity.getUserName().toUpperCase().equals(name) && nameList.contains(name)) ||
                (null != dependentProfile && !dependentProfile.getName().toUpperCase().equals(name) && nameList.contains(name))) {
            edt_Name.setError(getString(R.string.error_name_exist));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Mail.getText().toString()) && !new CommonHelper().isValidEmail(edt_Mail.getText().toString())) {
            edt_Mail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        if (TextUtils.isEmpty(tv_birthday.getText().toString())) {
            tv_birthday.setError(getString(R.string.error_dob_blank));
            isValid = false;
        }
        return isValid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void setDependentProfileImageUrl(String path) {
        List<JsonProfile> jsonProfiles = NoQueueBaseActivity.getUserProfile().getDependents();
        if (null != jsonProfiles && jsonProfiles.size() > 0) {
            for (int j = 0; j < jsonProfiles.size(); j++) {
                final JsonProfile jsonProfile = jsonProfiles.get(j);
                if (jsonProfile.getQueueUserId().equals(qUserId)) {
                    jsonProfiles.get(j).setProfileImage(path);
                    break;
                }
            }
        }
        JsonProfile jsonProfile = NoQueueBaseActivity.getUserProfile();
        jsonProfile.setDependents(jsonProfiles);
        NoQueueBaseActivity.setUserProfile(jsonProfile);

    }
}

