package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;

public interface CashFreeNotifyPresenter extends ResponseErrorPresenter {

    void cashFreeNotifyResponse(JsonPurchaseOrder jsonPurchaseOrder);

}