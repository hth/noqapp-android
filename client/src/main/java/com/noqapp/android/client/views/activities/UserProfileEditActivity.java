package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ClientProfileApiCall;
import com.noqapp.android.client.model.DependentApiCall;
import com.noqapp.android.client.presenter.DependencyPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserAddressList;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.CustomCalendar;
import com.noqapp.android.common.utils.FileUtils;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.common.utils.ShowUploadImageDialog;
import com.noqapp.android.common.views.activities.helper.CapitalizeEachWordFirstLetterTextWatcher;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserProfileEditActivity extends ProfileActivity implements View.OnClickListener,
        ImageUploadPresenter, ProfilePresenter, DependencyPresenter {
    private static final String TAG = UserProfileEditActivity.class.getSimpleName();

    private ImageView iv_profile;
    private String gender = "";
    private TextView tv_name;
    private TextView tv_birthday;
    private TextView tvAddress;
    private Button btn_update;
    private EditText edt_phoneNo;
    private EditText edt_Name;
    private EditText edt_Mail;
    private TextView tv_male;
    private TextView tv_female;
    private TextView tv_transgender;
    private TextView tv_remove_image;
    private boolean isDependent = false;
    private JsonProfile dependentProfile = null;
    private ClientProfileApiCall clientProfileApiCall;
    private List<String> nameList = new ArrayList<>();
    private String imageUrl = "";
    private String qUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        tv_name = findViewById(R.id.tv_name);
        tv_birthday = findViewById(R.id.tv_birthday);
        tvAddress = findViewById(R.id.tv_address);
        btn_update = findViewById(R.id.btn_update);
        edt_phoneNo = findViewById(R.id.edt_phone);
        edt_Name = findViewById(R.id.edt_name);
        edt_Name.addTextChangedListener(new CapitalizeEachWordFirstLetterTextWatcher(edt_Name));
        edt_Mail = findViewById(R.id.edt_email);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);
        tv_transgender = findViewById(R.id.tv_transgender);
        tv_remove_image = findViewById(R.id.tv_remove_image);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_edit_profile));
        iv_profile = findViewById(R.id.iv_profile);
        clientProfileApiCall = new ClientProfileApiCall();
        iv_profile.setOnClickListener(this);
        setProgressMessage("Updating profile....");
        isDependent = getIntent().getBooleanExtra(IBConstant.IS_DEPENDENT, false);
        dependentProfile = (JsonProfile) getIntent().getSerializableExtra(IBConstant.DEPENDENT_PROFILE);
        // gaurdianProfile = (JsonProfile) getIntent().getSerializableExtra(NoQueueBaseActivity.KEY_USER_PROFILE);
        nameList = getIntent().getStringArrayListExtra("nameList");

        updateUI();
        tv_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_transgender.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        tv_remove_image.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
    }


    private void loadProfilePic() {
        Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get()
                        .load(AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, imageUrl))
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
            new CustomToast().showToast(this, "Profile image change successfully! Change will be reflect after 5 min");
            tv_remove_image.setVisibility(View.VISIBLE);
        } else {
            new CustomToast().showToast(this, "Failed to update profile image");
        }
    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
            tv_remove_image.setVisibility(View.GONE);
            if (isDependent) {
                setDependentProfileImageUrl("");
            } else {
                AppInitialize.setUserProfileUri("");
            }
            new CustomToast().showToast(this, "Profile image removed successfully!");
        } else {
            new CustomToast().showToast(this, "Failed to remove profile image");
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
                showProgress();
                setProgressMessage("Removing profile image");
                clientProfileApiCall.setImageUploadPresenter(this);
                clientProfileApiCall.removeImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new UpdateProfile().setQueueUserId(qUserId));
            }
            break;
            case R.id.iv_profile:
                selectImage();
                break;
            case R.id.btn_update:
                updateProfile();
                break;

            case R.id.tv_birthday:
                CustomCalendar customCalendar = new CustomCalendar(UserProfileEditActivity.this);
                customCalendar.setDateSelectListener(new CustomCalendar.DateSelectListener() {
                    @Override
                    public void calendarDate(String date) {
                        tv_birthday.setText(date);
                    }
                });
                customCalendar.showDobCalendar();

//                Intent in = new Intent(UserProfileEditActivity.this, DatePickerActivity.class);
//                startActivityForResult(in, Constants.RC_DATE_PICKER);
                break;
            case R.id.tv_male:
                gender = "M";
                tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_transgender.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setBackgroundColor(ContextCompat.getColor(UserProfileEditActivity.this, R.color.review_color));
                tv_male.setText(getString(R.string.male));
                tv_male.setTextColor(Color.WHITE);
                tv_female.setTextColor(Color.BLACK);
                tv_transgender.setTextColor(Color.BLACK);
                break;
            case R.id.tv_female:
                gender = "F";
                tv_female.setBackgroundColor(ContextCompat.getColor(UserProfileEditActivity.this, R.color.review_color));
                tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_transgender.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setTextColor(Color.BLACK);
                tv_female.setTextColor(Color.WHITE);
                tv_transgender.setTextColor(Color.BLACK);
                tv_female.setText(getString(R.string.female));
                break;
            case R.id.tv_transgender:
                gender = "T";
                tv_transgender.setBackgroundColor(ContextCompat.getColor(UserProfileEditActivity.this, R.color.review_color));
                tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setTextColor(Color.BLACK);
                tv_female.setTextColor(Color.BLACK);
                tv_transgender.setTextColor(Color.WHITE);
                tv_transgender.setText(getString(R.string.transgender));
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
                    String convertedPath = new FileUtils().getFilePath(UserProfileEditActivity.this, data.getData());
                    if (StringUtils.isBlank(convertedPath)) {
                        throw new RuntimeException("Failed to find path for image");
                    }
                    if (!TextUtils.isEmpty(convertedPath)) {
                        ShowUploadImageDialog uploadImageDialog = new ShowUploadImageDialog(UserProfileEditActivity.this);
                        uploadImageDialog.setDialogClickListener(new ShowUploadImageDialog.DialogClickListener() {
                            @Override
                            public void btnPositiveClick() {
                                iv_profile.setImageBitmap(bitmap);
                                if (isDependent) {
                                    setDependentProfileImageUrl(convertedPath);
                                } else {
                                    AppInitialize.setUserProfileUri(convertedPath);
                                }
                                showProgress();
                                setProgressMessage("Updating profile image");
                                String type = getMimeType(UserProfileEditActivity.this, selectedImage);
                                File file = new File(convertedPath);
                                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                                RequestBody profileImageOfQid = RequestBody.create(MediaType.parse("text/plain"), qUserId);
                                clientProfileApiCall.setImageUploadPresenter(UserProfileEditActivity.this);
                                clientProfileApiCall.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, profileImageOfQid);

                            }

                            @Override
                            public void btnNegativeClick() {
                                //Do nothing
                            }
                        });
                        uploadImageDialog.displayDialog(bitmap);
                    }
                } catch (Exception e) {
                    Log.e("Failed getting image ", e.getLocalizedMessage(), e);
                    FirebaseCrashlytics.getInstance().log("Failed to find and upload profile image " + e.getLocalizedMessage());
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        } else if (requestCode == Constants.RC_DATE_PICKER && resultCode == Activity.RESULT_OK) {
            String date = data.getStringExtra("result");
            if (!TextUtils.isEmpty(date) && CommonHelper.isDateBeforeToday(this, date)) {
                tv_birthday.setText(date);
            }
        }
    }


    public void updateProfile() {
        if (validate()) {
            btn_update.setBackgroundResource(R.drawable.btn_bg_enable);
            btn_update.setTextColor(Color.WHITE);
            if (isOnline()) {
                showProgress();
                clientProfileApiCall.setProfilePresenter(this);
                //   String phoneNo = edt_phoneNo.getText().toString();
                String name = edt_Name.getText().toString();
                //   String mail = edt_Mail.getText().toString();
                String birthday = tv_birthday.getText().toString();
                if (isDependent) {
                    if (null != dependentProfile) {
                        UpdateProfile updateProfile = new UpdateProfile();
                        updateProfile.setFirstName(name);
                        updateProfile.setBirthday(AppUtils.convertDOBToValidFormat(birthday));
                        updateProfile.setGender(gender);
                        updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                        updateProfile.setQueueUserId(dependentProfile.getQueueUserId());
                        clientProfileApiCall.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                    } else {
                        Registration registration = new Registration();
                        registration.setPhone(PhoneFormatterUtil.phoneNumberWithCountryCode(AppInitialize.getPhoneNo(), AppInitialize.getCountryShortName()));
                        registration.setFirstName(name);
                        registration.setMail("");
                        registration.setPassword("");
                        registration.setBirthday(AppUtils.convertDOBToValidFormat(birthday));
                        registration.setGender(gender);
                        registration.setTimeZoneId(TimeZone.getDefault().getID());
                        registration.setCountryShortName(AppInitialize.getCountryShortName());
                        registration.setInviteCode("");
                        DependentApiCall dependentModel = new DependentApiCall(this);
                        dependentModel.addDependency(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), registration);
                        if (AppUtils.isRelease()) {
                            AnalyticsEvents.logContentEvent(AnalyticsEvents.EVENT_DEPENDENT_ADDED);
                        }
                    }
                } else {
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setFirstName(name);
                    updateProfile.setBirthday(AppUtils.convertDOBToValidFormat(birthday));
                    updateProfile.setGender(gender);
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    updateProfile.setQueueUserId(AppInitialize.getUserProfile().getQueueUserId());
                    clientProfileApiCall.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }

    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        AppInitialize.commitProfile(profile, email, auth);
        dismissProgress();
        new CustomToast().showToast(this, "Profile updated successfully");
        finish();
    }

    @Override
    public void profileError() {
        dismissProgress();
    }


    @Override
    public void dependencyResponse(JsonProfile profile) {
        Log.v("JsonProfile", profile.toString());
        AppInitialize.setUserProfile(profile);
        dependentProfile = profile;
        dismissProgress();
        new CustomToast().showToast(this, "Dependent added successfully");
        // updateUI();
        finish();
    }

    private void updateUI() {
        if (isDependent) {
            onClick(tv_male);// set default
            if (null != dependentProfile) {
                edt_Name.setText(dependentProfile.getName());
                tv_name.setText(dependentProfile.getName());

                JsonUserAddress jsonUserAddress = dependentProfile.findPrimaryOrAnyExistingAddress();
                tvAddress.setText(null == jsonUserAddress ? "" : jsonUserAddress.getAddress());

                imageUrl = dependentProfile.getProfileImage();
                qUserId = dependentProfile.getQueueUserId();
                if (dependentProfile.getGender().name().equals("M")) {
                    onClick(tv_male);
                } else if (dependentProfile.getGender().name().equals("T")) {
                    onClick(tv_transgender);
                } else {
                    onClick(tv_female);
                }
                try {
                    tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(dependentProfile.getBirthday())));
                } catch (Exception e) {
                    Log.e("Failed getting depends DOB", e.getLocalizedMessage(), e);
                    FirebaseCrashlytics.getInstance().log("Failed getting depends DOB " + e.getLocalizedMessage());
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            } else {
                btn_update.setText("Add Family Members");
                setProgressMessage("Adding family member....");
                tv_toolbar_title.setText("Add Profile");
                iv_profile.setEnabled(false);
                iv_profile.setClickable(false);
                imageUrl = "";
                qUserId = "";
            }
        } else {
            edt_Name.setText(AppInitialize.getUserName());
            tv_name.setText(AppInitialize.getUserName());
            tvAddress.setText(AppInitialize.getAddress());
            imageUrl = AppInitialize.getUserProfileUri();
            qUserId = AppInitialize.getUserProfile().getQueueUserId();
            if (AppInitialize.getGender().equals("M")) {
                onClick(tv_male);
            } else if (AppInitialize.getGender().equals("T")) {
                onClick(tv_transgender);
            } else {
                onClick(tv_female);
            }
            try {
                tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(AppInitialize.getUserDOB())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        edt_phoneNo.setText(AppInitialize.getPhoneNo());
        edt_Mail.setText(AppInitialize.getActualMail());
        edt_phoneNo.setEnabled(false);
        edt_Mail.setEnabled(false);

        loadProfilePic();
    }

    private boolean validate() {
        btn_update.setBackgroundResource(R.drawable.btn_bg_inactive);
        btn_update.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        boolean isValid = true;
        edt_Name.setError(null);
        edt_Mail.setError(null);
        tv_birthday.setError(null);
        AppUtils.hideKeyBoard(this);
        String name = edt_Name.getText().toString().toUpperCase();
        if (TextUtils.isEmpty(name)) {
            edt_Name.setError(getString(R.string.error_name_blank));
            isValid = false;
        }
        if (!TextUtils.isEmpty(name) && name.length() < 3) {
            edt_Name.setError(getString(R.string.error_name_length));
            isValid = false;
        }
        if (((null == dependentProfile && isDependent && nameList.contains(name)))
                || (null == dependentProfile && !isDependent && !AppInitialize.getUserName().toUpperCase().equals(name) && nameList.contains(name))
                || (null != dependentProfile && !dependentProfile.getName().toUpperCase().equals(name) && nameList.contains(name))) {
            edt_Name.setError(getString(R.string.error_name_exist));
            isValid = false;
        }
        if (!TextUtils.isEmpty(edt_Mail.getText().toString()) && !CommonHelper.isValidEmail(edt_Mail.getText().toString())) {
            edt_Mail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }
        if (TextUtils.isEmpty(tv_birthday.getText().toString())) {
            tv_birthday.setError(getString(R.string.error_dob_blank));
            isValid = false;
        } else {
            if (TextUtils.isEmpty(AppUtils.convertDOBToValidFormat(tv_birthday.getText().toString()))) {
                tv_birthday.setError(getString(R.string.error_dob_format));
                isValid = false;
            }
        }
        return isValid;
    }

    private void setDependentProfileImageUrl(String path) {
        List<JsonProfile> jsonProfiles = AppInitialize.getUserProfile().getDependents();
        if (null != jsonProfiles && jsonProfiles.size() > 0) {
            for (int j = 0; j < jsonProfiles.size(); j++) {
                final JsonProfile jsonProfile = jsonProfiles.get(j);
                if (jsonProfile.getQueueUserId().equals(qUserId)) {
                    jsonProfiles.get(j).setProfileImage(path);
                    break;
                }
            }
        }
        JsonProfile jsonProfile = AppInitialize.getUserProfile();
        jsonProfile.setDependents(jsonProfiles);
        AppInitialize.setUserProfile(jsonProfile);

    }
}

