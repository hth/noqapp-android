package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.common.beans.JsonProfile;

public interface ProfilePresenter {

    void profileResponse(JsonProfile jsonProfile, String mail, String auth);

    void profileError();

    void profileError(String error);

    void authenticationFailure(int errorCode);
}
