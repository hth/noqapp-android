package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface OrderProcessedPresenter extends ResponseErrorPresenter{

    void orderProcessedResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void orderProcessedError();
}
