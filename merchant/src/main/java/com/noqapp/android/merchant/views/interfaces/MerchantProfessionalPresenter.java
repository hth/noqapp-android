package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;

public interface MerchantProfessionalPresenter {

    void merchantProfessionalResponse(JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal);

    void merchantProfessionalError();

    void authenticationFailure(int errorCode);
}
