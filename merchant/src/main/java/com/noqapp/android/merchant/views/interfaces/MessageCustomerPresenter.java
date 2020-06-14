package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface MessageCustomerPresenter extends ResponseErrorPresenter {
    void messageCustomerResponse(JsonResponse jsonResponse);
}
