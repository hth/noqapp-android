package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;

public interface PurchaseOrderPresenter extends ResponseErrorPresenter {

    void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void purchaseOrderError();
}
