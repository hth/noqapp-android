package com.noqapp.merchant.views.interfaces;


import com.noqapp.merchant.presenter.beans.JsonMerchant;

public interface MerchantPresenter {

    void merchantResponse(JsonMerchant jsonMerchant);

    void merchantError();
}
