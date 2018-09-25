package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonToken;


public interface DispenseTokenPresenter extends ResponseErrorPresenter{

    void dispenseTokenResponse(JsonToken token);
}
