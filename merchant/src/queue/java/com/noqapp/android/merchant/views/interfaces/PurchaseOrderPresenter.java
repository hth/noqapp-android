package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.order.JsonPurchaseOrderList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface PurchaseOrderPresenter extends ResponseErrorPresenter {

    void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void purchaseOrderError();
}
