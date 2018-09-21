package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;

public interface OrderProcessedPresenter extends ResponseErrorPresenter{

    void orderProcessedResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void orderProcessedError();

    void authenticationFailure(int errorCode);
}
