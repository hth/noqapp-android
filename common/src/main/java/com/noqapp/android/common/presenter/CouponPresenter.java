package com.noqapp.android.common.presenter;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.common.beans.JsonCouponList;

public interface CouponPresenter extends ResponseErrorPresenter {

    void couponResponse(JsonCouponList jsonCouponList);

}
