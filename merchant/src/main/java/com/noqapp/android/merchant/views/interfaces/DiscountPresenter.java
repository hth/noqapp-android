package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonDiscountList;

public interface DiscountPresenter extends ResponseErrorPresenter {

    void discountResponse(JsonDiscountList jsonDiscountList);

}
