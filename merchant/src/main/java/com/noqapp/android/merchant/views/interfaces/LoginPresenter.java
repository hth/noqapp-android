package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface LoginPresenter extends ResponseErrorPresenter{
    void loginResponse(String email, String auth);

    void loginError();
}
