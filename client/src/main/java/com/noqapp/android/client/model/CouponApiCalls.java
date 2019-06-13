package com.noqapp.android.client.model;

import android.util.Log;
import androidx.annotation.NonNull;
import com.noqapp.android.client.model.response.api.CouponApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.CouponApplyPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonCouponList;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.CouponPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponApiCalls {
    private static final String TAG = CouponApiCalls.class.getSimpleName();

    private static final CouponApiUrls couponApiUrls;
    private CouponPresenter couponPresenter;
    private CouponApplyPresenter couponApplyPresenter;

    public void setCouponApplyPresenter(CouponApplyPresenter couponApplyPresenter) {
        this.couponApplyPresenter = couponApplyPresenter;
    }

    public void setCouponPresenter(CouponPresenter couponPresenter) {
        this.couponPresenter = couponPresenter;
    }

    static {
        couponApiUrls = RetrofitClient.getClient().create(CouponApiUrls.class);
    }

    public void availableCoupon(String did, String mail, String auth) {
        couponApiUrls.availableCoupon(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonCouponList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCouponList> call, @NonNull Response<JsonCouponList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        couponPresenter.couponResponse(response.body());
                        Log.d("availableCoupon", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty availableCoupon");
                        couponPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        couponPresenter.authenticationFailure();
                    } else {
                        couponPresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonCouponList> call, @NonNull Throwable t) {
                Log.e("onFailure availcoupon", t.getLocalizedMessage(), t);
                couponPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void apply(String did, String mail, String auth, CouponOnOrder couponOnOrder) {
        couponApiUrls.apply(did, Constants.DEVICE_TYPE, mail, auth,couponOnOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        couponApplyPresenter.couponApplyResponse(response.body());
                        Log.d("availableCoupon", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty availableCoupon");
                        couponApplyPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        couponApplyPresenter.authenticationFailure();
                    } else {
                        couponApplyPresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("onFailure availcoupon", t.getLocalizedMessage(), t);
                couponPresenter.responseErrorPresenter(null);
            }
        });
    }
}

