package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;

public interface OrderProcessedPresenter {

    void orderProcessedResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void orderProcessedError();

    void authenticationFailure(int errorCode);
}
