package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.common.beans.JsonProfile;

public interface ProfilePresenter {

    void profileResponse(JsonProfile jsonProfile,final String mail, final String auth);

    void profileError();

    void authenticationFailure(int errorCode);
}
