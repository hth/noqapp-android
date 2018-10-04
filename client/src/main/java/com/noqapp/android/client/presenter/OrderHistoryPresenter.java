package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.order.JsonPurchaseOrderList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface OrderHistoryPresenter extends ResponseErrorPresenter {

    void orderHistoryResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void orderHistoryError();
}