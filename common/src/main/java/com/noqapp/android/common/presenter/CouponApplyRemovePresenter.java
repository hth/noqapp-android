package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;

public interface CouponApplyRemovePresenter extends ResponseErrorPresenter {

    void couponApplyResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void couponRemoveResponse(JsonPurchaseOrder jsonPurchaseOrder);
}
