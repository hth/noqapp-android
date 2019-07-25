package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface PurchaseOrderPresenter extends ResponseErrorPresenter{

    void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void payCashResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void purchaseOrderCancelResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void purchaseOrderActivateResponse(JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical);
}
