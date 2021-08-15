package com.noqapp.android.client.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.ClientProfileApiCall;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserAddressList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.GenderEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.CommonHelper;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends ProfileActivity implements View.OnClickListener, ProfilePresenter {
    private TextView tv_name;
    private TextView tv_birthday;
    private TextView tvAddress;
    private EditText edt_phoneNo;
    private EditText edt_Name;
    private EditText edt_Mail;
    private TextView tv_male;
    private TextView tv_age;
    private TextView tv_female;
    private TextView tv_info;
    private TextView tv_email_verification;
    private TextView tv_modify_email;
    private LinearLayout ll_dependent;
    private ImageView iv_profile;
    private String gender = "";
    private TextView tv_transgender;
    private ArrayList<String> nameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        tv_name = findViewById(R.id.tv_name);
        ImageView iv_edit = findViewById(R.id.iv_edit);
        ImageView iv_edit_mail = findViewById(R.id.iv_edit_mail);
        ImageView iv_add_dependent = findViewById(R.id.iv_add_dependent);
        ImageView ivEditAddress = findViewById(R.id.iv_edit_address);
        tv_birthday = findViewById(R.id.tv_birthday);
        tvAddress = findViewById(R.id.tv_address);
        edt_phoneNo = findViewById(R.id.edt_phone);
        edt_Name = findViewById(R.id.edt_name);
        edt_Mail = findViewById(R.id.edt_email);
        tv_age = findViewById(R.id.tv_age);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);
        tv_info = findViewById(R.id.tv_info);
        tv_transgender = findViewById(R.id.tv_transgender);
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
        ivEditAddress.setOnClickListener(this);
        iv_add_dependent.setOnClickListener(v -> {
            Intent in = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
            in.putExtra(IBConstant.IS_DEPENDENT, true);
            in.putStringArrayListExtra("nameList", nameList);
            startActivity(in);
        });

        if (isOnline()) {
            showProgress();
            ClientProfileApiCall clientProfileApiCall = new ClientProfileApiCall();
            clientProfileApiCall.setProfilePresenter(this);
            clientProfileApiCall.fetchProfile(UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void loadProfilePic() {
        AppUtils.loadProfilePic(iv_profile, AppInitialize.getUserProfileUri(), this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_profile:
            case R.id.iv_edit:
                Intent in = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
                in.putExtra(IBConstant.IS_DEPENDENT, false);
                in.putStringArrayListExtra("nameList", nameList);
                startActivity(in);
                break;

            case R.id.iv_edit_address:
                Intent addAddressIntent = new Intent(this, AddAddressActivity.class);
                startActivityForResult(addAddressIntent, Constants.REQUEST_CODE_ADD_ADDRESS);
                break;

            case R.id.tv_migrate:
                Intent migrate = new Intent(this, MigrateActivity.class);
                startActivity(migrate);
                break;
            case R.id.edt_email:
            case R.id.iv_edit_mail:
            case R.id.tv_modify_email:
                Intent changeEmail = new Intent(this, ChangeEmailActivity.class);
                changeEmail.putExtra("email", edt_Mail.getText().toString());
                changeEmail.putExtra("isValidated", AppInitialize.getUserProfile().isAccountValidated());
                startActivity(changeEmail);
                break;

        }
    }


    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        AppInitialize.commitProfile(profile, email, auth);
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
        if (null != eej) {
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.getCode())) {
                new CustomToast().showToast(this, getString(R.string.error_account_block));
                AppInitialize.clearPreferences();
                dismissProgress();
                finish();//close the current activity
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
    }

    private void updateUI() {
        if (AppInitialize.getUserProfile() != null && AppInitialize.getUserProfile().getUserLevel() != null) {
            if (AppInitialize.getUserProfile().getUserLevel() == UserLevelEnum.S_MANAGER) {
                tv_info.setText("Max 10 allowed");
            } else {
                tv_info.setText("Max 5 allowed");
            }
        } else {
            /* Force logout when User Profile is null. */
            authenticationFailure();
        }

        edt_Name.setText(AppInitialize.getUserName());
        tv_name.setText(AppInitialize.getUserName());
        edt_phoneNo.setText(AppInitialize.getPhoneNo());
        edt_Mail.setText(AppInitialize.getActualMail());
        tv_email_verification.setVisibility(AppInitialize.showEmailVerificationField() ? View.VISIBLE : View.GONE);
        tv_modify_email.setVisibility(AppInitialize.getUserProfile().isAccountValidated() ? View.GONE : View.VISIBLE);
        if (AppInitialize.getMail().endsWith(Constants.MAIL_NOQAPP_COM)) {
            tv_email_verification.setVisibility(View.VISIBLE);
            tv_email_verification.setText("Please add your Email Id");
        }
        edt_phoneNo.setEnabled(false);
        edt_Mail.setFocusable(false);
        edt_Mail.setClickable(true);
        edt_Name.setEnabled(false);
        tv_birthday.setEnabled(false);
        tvAddress.setText(AppInitialize.getAddress());
        int id;
        if (AppInitialize.getGender().equals(GenderEnum.M.name())) {
            id = R.id.tv_male;
        } else if (AppInitialize.getGender().equals(GenderEnum.T.name())) {
            id = R.id.tv_transgender;
        } else {
            id = R.id.tv_female;
        }
        switch (id) {
            case R.id.tv_male:
                gender = GenderEnum.M.name();
                tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_transgender.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, R.color.review_color));
                tv_male.setText(getString(R.string.male));
                tv_male.setTextColor(Color.WHITE);
                tv_female.setTextColor(Color.BLACK);
                tv_transgender.setTextColor(Color.BLACK);
                break;
            case R.id.tv_female:
                gender = GenderEnum.F.name();
                tv_female.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, R.color.review_color));
                tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_transgender.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setTextColor(Color.BLACK);
                tv_female.setTextColor(Color.WHITE);
                tv_transgender.setTextColor(Color.BLACK);
                tv_female.setText(getString(R.string.female));
                break;
            case R.id.tv_transgender:
                gender = GenderEnum.T.name();
                tv_transgender.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, R.color.review_color));
                tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setTextColor(Color.BLACK);
                tv_female.setTextColor(Color.BLACK);
                tv_transgender.setTextColor(Color.WHITE);
                tv_transgender.setText(getString(R.string.transgender));
                break;
        }
        try {
            tv_birthday.setText(CommonHelper.SDF_DOB_FROM_UI.format(CommonHelper.SDF_YYYY_MM_DD.parse(AppInitialize.getUserDOB())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<JsonProfile> jsonProfiles = AppInitialize.getUserProfile().getDependents();
        nameList.clear();
        nameList.add(AppInitialize.getUserName().toUpperCase());
        ll_dependent.removeAllViews();
        if (null != jsonProfiles && jsonProfiles.size() > 0) {
            for (int j = 0; j < jsonProfiles.size(); j++) {
                final JsonProfile jsonProfile = jsonProfiles.get(j);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View listitem_dependent = inflater.inflate(R.layout.listitem_dependent, null);
                ImageView iv_edit = listitem_dependent.findViewById(R.id.iv_edit);
                TextView tv_title = listitem_dependent.findViewById(R.id.tv_title);
                tv_title.setText(jsonProfile.getName());
                iv_edit.setOnClickListener(v -> {
                    Intent in = new Intent(UserProfileActivity.this, UserProfileEditActivity.class);
                    in.putExtra(IBConstant.IS_DEPENDENT, true);
                    in.putExtra(IBConstant.DEPENDENT_PROFILE, jsonProfile);
                    in.putStringArrayListExtra("nameList", nameList);
                    startActivity(in);
                });
                ll_dependent.addView(listitem_dependent);
                nameList.add(jsonProfile.getName().toUpperCase());
            }
        }
        loadProfilePic();
        tv_age.setText(CommonHelper.calculateAge(AppInitialize.getUserDOB()) + " (" + gender + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_ADD_ADDRESS && resultCode == Activity.RESULT_OK) {
            JsonUserAddressList jsonUserAddressList = (JsonUserAddressList) data.getSerializableExtra(Constants.ADDRESS_LIST);
            for (JsonUserAddress address : jsonUserAddressList.getJsonUserAddresses()) {
                if (address.isPrimaryAddress()) {
                    tvAddress.setText(address.getAddress());
                }
            }
        }
    }
}
