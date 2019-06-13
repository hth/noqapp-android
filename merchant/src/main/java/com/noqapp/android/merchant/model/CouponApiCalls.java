package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.merchant.model.response.api.CouponApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonCouponList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CouponOnOrder;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.CouponOnOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.CouponPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponApiCalls {
    private static final CouponApiUrls couponApiUrls;
    private CouponPresenter couponPresenter;
    private CouponOnOrderPresenter couponOnOrderPresenter;

    public void setCouponPresenter(CouponPresenter couponPresenter) {
        this.couponPresenter = couponPresenter;
    }

    public void setCouponOnOrderPresenter(CouponOnOrderPresenter couponOnOrderPresenter) {
        this.couponOnOrderPresenter = couponOnOrderPresenter;
    }

    static {
        couponApiUrls = RetrofitClient.getClient().create(CouponApiUrls.class);
    }

    public void availableDiscount(String did, String mail, String auth, String codeQR) {
        couponApiUrls.availableCoupon(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonCouponList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCouponList> call, @NonNull Response<JsonCouponList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp availableCoupon", String.valueOf(response.body()));
                        couponPresenter.couponResponse(response.body());
                    } else {
                        Log.e("error availableCoupon", "Error availableCoupon" + response.body().getError());
                        couponPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        couponPresenter.authenticationFailure();
                    } else {
                        couponPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonCouponList> call, @NonNull Throwable t) {
                Log.e("Fail availableCoupon ", t.getLocalizedMessage(), t);
                couponPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void apply(String did, String mail, String auth, CouponOnOrder couponOnOrder) {
        couponApiUrls.apply(did, Constants.DEVICE_TYPE, mail, auth, couponOnOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp applyDiscount", String.valueOf(response.body()));
                        couponOnOrderPresenter.discountOnOrderResponse(response.body());
                    } else {
                        Log.e("error applyDiscount", "Error applyDiscount" + response.body().getError());
                        couponOnOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        couponOnOrderPresenter.authenticationFailure();
                    } else {
                        couponOnOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("Fail applyDiscount ", t.getLocalizedMessage(), t);
                couponOnOrderPresenter.responseErrorPresenter(null);
            }
        });
    }
}
