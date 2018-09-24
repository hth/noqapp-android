package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.ImagePathReader;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.List;


public class UserProfileActivity extends ProfileActivity implements View.OnClickListener, ImageUploadPresenter, ProfilePresenter {
    private TextView tv_name;
    private TextView tv_birthday;
    private EditText edt_address;
    private EditText edt_phoneNo;
    private EditText edt_Name;
    private EditText edt_Mail;
    private TextView tv_male;
    private TextView tv_female;
    private TextView tv_info;
    private TextView tv_email_verification;
    private TextView tv_modify_email;
    private LinearLayout ll_dependent;
    private ImageView iv_profile;
    private String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        tv_name = findViewById(R.id.tv_name);
        ImageView iv_edit = findViewById(R.id.iv_edit);
        ImageView iv_edit_mail = findViewById(R.id.iv_edit_mail);
        ImageView iv_add_dependent = findViewById(R.id.iv_add_dependent);
        tv_birthday = findViewById(R.id.tv_birthday);
        edt_address = findViewById(R.id.edt_address);
        edt_phoneNo = findViewById(R.id.edt_phone);
        edt_Name = findViewById(R.id.edt_name);
        edt_Mail = findViewById(R.id.edt_email);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);
        tv_info = findViewById(R.id.tv_info);
        TextView tv_migrate = findViewById(R.id.tv_migrate);
        tv_email_verification = findViewById(R.id.tv_email_verification);
        tv_modify_email = findViewById(R.id.tv_modify_email);
        ll_dependent = findViewById(R.id.ll_dependent);
        initActionsViews(false);
        iv_profile = findViewById(R.id.iv_profile);
        iv_edit.setOnClickListener(this);
        iv_edit_mail.setOnClickListener(this);
        loadProfilePic();
        tv_toolbar_title.setText(getString(R.string.screen_profile));
        iv_profile.setOnClickListener(this);
        tv_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_migrate.setOnClickListener(this);
        edt_Mail.setOnClickListener(this);
        tv_modify_email.setOnClickListener(this);
        iv_add_dependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
                in.putExtra(NoQueueBaseActivity.IS_DEPENDENT, true);
                // in.putExtra(NoQueueBaseActivity.DEPENDENT_PROFILE,new JsonProfile());

                startActivity(in);
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            ProfileModel profileModel = new ProfileModel();
            profileModel.setProfilePresenter(this);
            profileModel.fetchProfile(UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
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
        dismissProgress();
        Log.v("Image upload", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Profile image change successful!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_LONG).show();
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
            case R.id.iv_profile:
                selectImage();
                break;
            case R.id.iv_edit:
                Intent in = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
                in.putExtra(NoQueueBaseActivity.IS_DEPENDENT, false);
                // in.putExtra(NoQueueBaseActivity.KEY_USER_PROFILE,LaunchActivity.getLaunchActivity().getUserProfile());
                startActivity(in);
                break;

            case R.id.tv_migrate:
                Intent migrate = new Intent(this, MigrateActivity.class);
                startActivity(migrate);
                break;
            case R.id.edt_email:
            case R.id.iv_edit_mail:
            case R.id.tv_modify_email:
                Intent changeEmail = new Intent(this, ChangeEmailActivity.class);
                startActivity(changeEmail);
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
                        progressDialog.show();
                        progressDialog.setMessage("Updating profile image");
                        String type = getMimeType(this, selectedImage);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody profileImageOfQid = RequestBody.create(MediaType.parse("text/plain"), NoQueueBaseActivity.getUserProfile().getQueueUserId());
                        ProfileModel profileModel = new ProfileModel();
                        profileModel.setImageUploadPresenter(this);
                        profileModel.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, profileImageOfQid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
        dismissProgress();
        updateUI();
    }


    @Override
    public void profileError() {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
        AppUtilities.authenticationProcessing(this, errorCode);
    }

    private void updateUI() {
        if (NoQueueBaseActivity.getUserProfile().getUserLevel() == UserLevelEnum.S_MANAGER) {
            tv_info.setText("Max 10 allowed");
        } else {
            tv_info.setText("Max 5 allowed");
        }
        edt_Name.setText(NoQueueBaseActivity.getUserName());
        tv_name.setText(NoQueueBaseActivity.getUserName());
        edt_phoneNo.setText(NoQueueBaseActivity.getPhoneNo());
        edt_Mail.setText(NoQueueBaseActivity.getActualMail());
        tv_email_verification.setVisibility(NoQueueBaseActivity.showEmailVerificationField(NoQueueBaseActivity.getUserProfile().isAccountValidated()) ? View.VISIBLE : View.GONE);
        tv_modify_email.setVisibility(NoQueueBaseActivity.getUserProfile().isAccountValidated() ? View.GONE : View.VISIBLE);
        if (NoQueueBaseActivity.getMail().endsWith(Constants.MAIL_NOQAPP_COM)) {
            tv_email_verification.setVisibility(View.VISIBLE);
            tv_email_verification.setText("Please add your Email Id");
        }
        edt_phoneNo.setEnabled(false);
        edt_Mail.setFocusable(false);
        edt_Mail.setClickable(true);
        edt_Name.setEnabled(false);
        tv_birthday.setEnabled(false);
        edt_address.setEnabled(false);
        edt_address.setText(NoQueueBaseActivity.getAddress());
        int id = 0;
        if (NoQueueBaseActivity.getGender().equals("M")) {
            id = R.id.tv_male;
        } else {
            id = R.id.tv_female;
        }
        switch (id) {
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
        try {
            tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(NoQueueBaseActivity.getUserDOB())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<JsonProfile> jsonProfiles = NoQueueBaseActivity.getUserProfile().getDependents();
        ll_dependent.removeAllViews();
        if (null != jsonProfiles && jsonProfiles.size() > 0) {
            for (int j = 0; j < jsonProfiles.size(); j++) {
                final JsonProfile jsonProfile = jsonProfiles.get(j);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View listitem_dependent = inflater.inflate(R.layout.listitem_dependent, null);
                ImageView iv_edit = listitem_dependent.findViewById(R.id.iv_edit);
                TextView tv_title = listitem_dependent.findViewById(R.id.tv_title);
                tv_title.setText(jsonProfile.getName());
                iv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
                        in.putExtra(NoQueueBaseActivity.IS_DEPENDENT, true);
                        in.putExtra(NoQueueBaseActivity.DEPENDENT_PROFILE, jsonProfile);
                        startActivity(in);
                    }
                });
                ll_dependent.addView(listitem_dependent);
            }
        }
        loadProfilePic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}

