package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistoricalList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface OrderHistoryPresenter extends ResponseErrorPresenter {

    void orderHistoryResponse(JsonPurchaseOrderHistoricalList jsonPurchaseOrderHistoricalList);
}