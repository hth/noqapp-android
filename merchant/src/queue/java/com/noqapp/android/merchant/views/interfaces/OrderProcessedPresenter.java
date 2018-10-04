package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.order.JsonPurchaseOrderList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface OrderProcessedPresenter extends ResponseErrorPresenter{

    void orderProcessedResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void orderProcessedError();
}
