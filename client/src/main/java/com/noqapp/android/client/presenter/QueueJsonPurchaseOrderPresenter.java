package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface QueueJsonPurchaseOrderPresenter extends ResponseErrorPresenter {
    void queueJsonPurchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder);
}
