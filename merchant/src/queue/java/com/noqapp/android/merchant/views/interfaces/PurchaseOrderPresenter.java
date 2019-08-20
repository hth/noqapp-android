package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface PurchaseOrderPresenter extends ResponseErrorPresenter {

    void purchaseOrderListResponse(JsonPurchaseOrderList jsonPurchaseOrderList);

    void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void purchaseOrderError();
}
