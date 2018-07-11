package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.common.beans.ErrorEncounteredJson;


public interface DispenseTokenPresenter {


    void dispenseTokenError(ErrorEncounteredJson errorEncounteredJson);

    void authenticationFailure(int errorCode);

    void dispenseTokenResponse(JsonToken token);
}
