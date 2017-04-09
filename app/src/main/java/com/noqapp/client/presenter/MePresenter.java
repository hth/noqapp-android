package com.noqapp.client.presenter;

import android.content.Context;

import com.noqapp.client.model.RegisterModel;
import com.noqapp.client.presenter.beans.Profile;
import com.noqapp.client.presenter.beans.body.Registration;
import com.noqapp.client.presenter.interfaces.MePresenterInterface;
import com.noqapp.client.views.interfaces.MeView;

/**
 * Created by omkar on 4/8/17.
 */

public class MePresenter implements ProfilePresenter {

    public MeView meView;
    public MePresenter(Context context)
    {

    }

    public void callProfile(Registration registration)
    {
        RegisterModel registerModel = new RegisterModel();
        registerModel.profilePresenter = this;
        registerModel.register(registration);
    }

    @Override
    public void queueResponse(Profile profile) {
        if(profile.getError() == null) {
            meView.queueResponse(profile);
        } else {
            //TODO show error message
        }
    }

    @Override
    public void queueError() {
      meView.queueError();
    }
}
