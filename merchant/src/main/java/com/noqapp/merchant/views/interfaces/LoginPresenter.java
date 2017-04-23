package com.noqapp.merchant.views.interfaces;


public interface LoginPresenter {
    void loginResponse(String email,String outh);

    void loginError();
}
