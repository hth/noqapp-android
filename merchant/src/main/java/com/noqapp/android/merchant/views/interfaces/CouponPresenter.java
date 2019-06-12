package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonCouponList;

public interface CouponPresenter extends ResponseErrorPresenter {

    void couponResponse(JsonCouponList jsonCouponList);

}
