package com.noqapp.android.client.presenter;


import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface PurchaseOrderPresenter extends ResponseErrorPresenter{

    void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void purchaseOrderError();
}
