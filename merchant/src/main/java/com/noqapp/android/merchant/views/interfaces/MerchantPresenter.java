package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;

public interface MerchantPresenter extends ResponseErrorPresenter{

    void merchantResponse(JsonMerchant jsonMerchant);

    void merchantError();

    void authenticationFailure(int errorCode);
}
