package com.noqapp.android.merchant.views.interfaces;


public interface LoginPresenter {
    void loginResponse(String email, String auth);

    void loginError();
}
