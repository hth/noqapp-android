package com.noqapp.android.client.presenter;

import android.content.Context;

import com.noqapp.android.client.model.RegisterModel;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.MeView;


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
    public void queueResponse(JsonProfile profile, String email, String auth) {
        // if(profile.getError() == null) {
        meView.queueResponse(profile, email, auth);
        // } else {
        //TODO show error message
        // }
    }

    @Override
    public void queueError() {
        meView.queueError();
    }
}
