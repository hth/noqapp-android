package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;

public interface PurchaseOrderPresenter {

    void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void purchaseOrderError();

    void authenticationFailure(int errorCode);
}
