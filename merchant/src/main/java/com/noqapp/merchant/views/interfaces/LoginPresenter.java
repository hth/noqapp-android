package com.noqapp.merchant.views.interfaces;


import com.noqapp.merchant.presenter.beans.JsonTopicList;


public interface LoginPresenter {
    void loginResponse(String email,String outh);

    void loginError();
}
