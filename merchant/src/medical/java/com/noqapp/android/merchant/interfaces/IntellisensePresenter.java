package com.noqapp.android.merchant.interfaces;

import com.noqapp.common.beans.JsonResponse;

public interface IntellisensePresenter {

    void intellisenseResponse(JsonResponse jsonResponse);

    void intellisenseError();

    void authenticationFailure(int errorCode);
}
