package com.noqapp.android.client.presenter;

import com.noqapp.android.client.model.RegisterModel;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.MeView;
import com.noqapp.android.common.beans.JsonProfile;

import android.content.Context;


public class MePresenter implements ProfilePresenter {

    public MeView meView;

    public MePresenter(Context context) {

    }

    public void callProfile(Registration registration) {
        new RegisterModel(this).register(UserUtils.getDeviceId(), registration);
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

    @Override
    public void queueError(String error) {

    }

    @Override
    public void authenticationFailure(int errorCode) {
        //TODO(chandra)
    }
}
