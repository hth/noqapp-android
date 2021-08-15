package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.ClientCouponApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.beans.body.Location;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonCouponList;
import com.noqapp.android.common.beans.body.CouponOnOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.presenter.CouponApplyRemovePresenter;
import com.noqapp.android.common.presenter.CouponPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientCouponApiCalls {
    private static final String TAG = ClientCouponApiCalls.class.getSimpleName();

    private static final ClientCouponApi CLIENT_COUPON_API;
    private CouponPresenter couponPresenter;
    private CouponApplyRemovePresenter couponApplyRemovePresenter;

    public void setCouponApplyRemovePresenter(CouponApplyRemovePresenter couponApplyRemovePresenter) {
        this.couponApplyRemovePresenter = couponApplyRemovePresenter;
    }

    public void setCouponPresenter(CouponPresenter couponPresenter) {
        this.couponPresenter = couponPresenter;
    }

    static {
        CLIENT_COUPON_API = RetrofitClient.getClient().create(ClientCouponApi.class);
    }

    public void globalCoupon(String did, String mail, String auth, Location location) {
        CLIENT_COUPON_API.globalCoupon(did, Constants.DEVICE_TYPE, mail, auth,location).enqueue(new Callback<JsonCouponList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCouponList> call, @NonNull Response<JsonCouponList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        couponPresenter.couponResponse(response.body());
                        Log.d("globalCoupon", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty globalCoupon");
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
                Log.e("onFailure globalCoupon", t.getLocalizedMessage(), t);
                couponPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void availableCoupon(String did, String mail, String auth) {
        CLIENT_COUPON_API.availableCoupon(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonCouponList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCouponList> call, @NonNull Response<JsonCouponList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
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

    public void filterCoupon(String did, String mail, String auth, String codeQR) {
        CLIENT_COUPON_API.filterCoupon(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonCouponList>() {
            @Override
            public void onResponse(@NonNull Call<JsonCouponList> call, @NonNull Response<JsonCouponList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        couponPresenter.couponResponse(response.body());
                        Log.d("filterCoupon", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty filterCoupon");
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
                Log.e("onFailure filterCoupon", t.getLocalizedMessage(), t);
                couponPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void apply(String did, String mail, String auth, CouponOnOrder couponOnOrder) {
        CLIENT_COUPON_API.apply(did, Constants.DEVICE_TYPE, mail, auth, couponOnOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        couponApplyRemovePresenter.couponApplyResponse(response.body());
                        Log.d("availableCoupon", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty availableCoupon");
                        couponApplyRemovePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        couponApplyRemovePresenter.authenticationFailure();
                    } else {
                        couponApplyRemovePresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("onFailure availcoupon", t.getLocalizedMessage(), t);
                couponApplyRemovePresenter.responseErrorPresenter(null);
            }
        });
    }

    public void remove(String did, String mail, String auth, CouponOnOrder couponOnOrder) {
        CLIENT_COUPON_API.remove(did, Constants.DEVICE_TYPE, mail, auth, couponOnOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        couponApplyRemovePresenter.couponRemoveResponse(response.body());
                        Log.d("removeCoupon", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty removeCoupon");
                        couponApplyRemovePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        couponApplyRemovePresenter.authenticationFailure();
                    } else {
                        couponApplyRemovePresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("onFailure removeCoupon", t.getLocalizedMessage(), t);
                couponApplyRemovePresenter.responseErrorPresenter(null);
            }
        });
    }
}

