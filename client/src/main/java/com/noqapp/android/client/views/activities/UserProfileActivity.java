package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 10/4/18.
 */

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.interfaces.ProfilePresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.ImagePathReader;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class UserProfileActivity extends ProfileActivity implements View.OnClickListener, ImageUploadPresenter, ProfilePresenter {

    @BindView(R.id.tv_name)
    protected TextView tv_name;
    @BindView(R.id.iv_edit)
    protected ImageView iv_edit;
    @BindView(R.id.iv_add_dependent)
    protected ImageView iv_add_dependent;
    @BindView(R.id.edt_birthday)
    protected EditText edt_birthday;
    @BindView(R.id.edt_address)
    protected EditText edt_address;
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
    @BindView(R.id.tv_email_verification)
    protected TextView tv_email_verification;
    @BindView(R.id.tv_modify_email)
    protected TextView tv_modify_email;
    @BindView(R.id.ll_gender)
    protected LinearLayout ll_gender;
    @BindView(R.id.ll_dependent)
    protected LinearLayout ll_dependent;

    private SimpleDateFormat dateFormatter;
    public ImageView iv_profile;
    public String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        initActionsViews(false);
        iv_profile = findViewById(R.id.iv_profile);
        iv_edit.setOnClickListener(this);
        loadProfilePic();
        tv_toolbar_title.setText(getString(R.string.screen_profile));
        iv_profile.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        //updateUI();
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_migrate.setOnClickListener(this);
        edt_Mail.setOnClickListener(this);
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
    public void queueResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
        dismissProgress();
        updateUI();
    }


    @Override
    public void queueError() {
        dismissProgress();
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
        tv_name.setText(NoQueueBaseActivity.getUserName());
        edt_phoneNo.setText(NoQueueBaseActivity.getPhoneNo());
        edt_Mail.setText(NoQueueBaseActivity.getActualMail());
        tv_email_verification.setVisibility(NoQueueBaseActivity.showEmailVerificationField(NoQueueBaseActivity.getUserProfile().isAccountValidated()) ? View.VISIBLE : View.GONE);
        tv_modify_email.setVisibility(NoQueueBaseActivity.getUserProfile().isAccountValidated() ? View.GONE : View.VISIBLE);
        if (NoQueueBaseActivity.getMail().contains("noqapp.com")) {
            tv_email_verification.setVisibility(View.VISIBLE);
            tv_email_verification.setText("Please add your email ID");
        }
        edt_phoneNo.setEnabled(false);
        edt_Mail.setFocusable(false);
        edt_Mail.setClickable(true);
        edt_Name.setEnabled(false);
        edt_birthday.setEnabled(false);
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
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = dateFormatter.format(fromUser.parse(NoQueueBaseActivity.getUserDOB()));
            edt_birthday.setText(reformattedStr);
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

