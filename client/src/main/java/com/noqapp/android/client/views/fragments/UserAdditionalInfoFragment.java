package com.noqapp.android.client.views.fragments;

/**
 * Created by chandra on 10/4/18.
 */


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.presenter.beans.body.UpdateProfile;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UserAdditionalInfoFragment extends Fragment implements View.OnClickListener, ProfilePresenter {


    @BindView(R.id.edt_birthday)
    protected EditText edt_birthday;
    @BindView(R.id.btn_update)
    protected Button btn_update;
    @BindView(R.id.edt_phone)
    protected EditText edt_phoneNo;
    @BindView(R.id.edt_name)
    protected EditText edt_Name;
    @BindView(R.id.edt_email)
    protected EditText edt_Mail;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_additional_info, container, false);
        ButterKnife.bind(this, view);
        updateUI();
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (LaunchActivity.getLaunchActivity().isOnline()) {
//            //progressDialog.show();
//            ProfileModel.profilePresenter = this;
//            ProfileModel.fetchProfile(UserUtils.getEmail(), UserUtils.getAuth());
//        } else {
//            ShowAlertInformation.showNetworkDialog(getActivity());
//        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {


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

                UpdateProfile updateProfile = new UpdateProfile();

                updateProfile.setFirstName(name);
                updateProfile.setBirthday(AppUtilities.convertDOBToValidFormat(birthday));

                updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                ProfileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }

    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
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
        edt_Mail.setText(NoQueueBaseActivity.getMail());
        edt_phoneNo.setEnabled(false);
        edt_Mail.setEnabled(false);
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
