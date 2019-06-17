package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface CouponApplyRemovePresenter extends ResponseErrorPresenter {

    void couponApplyResponse(JsonPurchaseOrder jsonPurchaseOrder);

    void couponRemoveResponse(JsonPurchaseOrder jsonPurchaseOrder);
}
