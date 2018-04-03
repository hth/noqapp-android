package com.noqapp.android.client.presenter.interfaces;


import com.noqapp.android.client.presenter.beans.JsonResponse;

public interface PurchaseOrderPresenter {

    void purchaseOrderResponse(JsonResponse jsonResponse);

    void purchaseOrderError();


    void authenticationFailure(int errorCode);
}
