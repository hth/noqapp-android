package com.noqapp.android.client.presenter.interfaces;


import com.noqapp.android.common.beans.order.JsonPurchaseOrder;

public interface PurchaseOrderPresenter {

    void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void purchaseOrderError();

    void authenticationFailure(int errorCode);
}
