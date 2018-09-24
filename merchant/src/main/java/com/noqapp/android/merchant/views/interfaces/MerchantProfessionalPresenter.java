package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MerchantProfessionalPresenter extends ResponseErrorPresenter{

    void merchantProfessionalResponse(JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal);

    void merchantProfessionalError();
}
