package com.noqapp.android.client.presenter;


import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface PurchaseOrderPresenter extends ResponseErrorPresenter{

    void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void purchaseOrderCancelResponse(JsonPurchaseOrder jsonPurchaseOrder);

}
