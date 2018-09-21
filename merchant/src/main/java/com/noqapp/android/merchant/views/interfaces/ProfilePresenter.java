package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface ProfilePresenter extends ResponseErrorPresenter{

    void profileResponse(JsonProfile jsonProfile, String mail, String auth);

    void profileError();

    void authenticationFailure(int errorCode);
}
