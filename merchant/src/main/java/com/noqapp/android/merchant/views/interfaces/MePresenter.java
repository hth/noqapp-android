package com.noqapp.android.merchant.views.interfaces;

import android.content.Context;

import com.noqapp.android.merchant.model.RegisterModel;
import com.noqapp.android.merchant.presenter.beans.body.Registration;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.common.beans.JsonProfile;


public class MePresenter implements ProfilePresenter {

    public MeView meView;

    public MePresenter(Context context) {

    }

    public void callProfile(Registration registration) {
        RegisterModel registerModel = new RegisterModel();
        registerModel.profilePresenter = this;
        registerModel.register(UserUtils.getDeviceId(), registration);
    }

    @Override
    public void profileResponse(JsonProfile jsonProfile, String mail, String auth) {

    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        // if(profile.getError() == null) {
        meView.queueResponse(profile, email, auth);
        // } else {
        //TODO show error message
        // }
    }

    @Override
    public void profileError() {

    }

    @Override
    public void profileError(String error) {

    }

    @Override
    public void authenticationFailure(int errorCode) {
        //TODO(chandra)
    }
}
