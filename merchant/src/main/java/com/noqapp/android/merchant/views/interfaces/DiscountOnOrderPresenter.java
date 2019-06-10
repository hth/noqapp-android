package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface DiscountOnOrderPresenter extends ResponseErrorPresenter {

    void discountOnOrderResponse(JsonPurchaseOrder jsonPurchaseOrder);

}
