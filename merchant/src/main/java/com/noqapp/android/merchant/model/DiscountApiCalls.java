package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.merchant.model.response.api.DiscountApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonDiscountList;
import com.noqapp.android.merchant.presenter.beans.body.merchant.DiscountOnOrder;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.DiscountOnOrderPresenter;
import com.noqapp.android.merchant.views.interfaces.DiscountPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DiscountApiCalls {
    private static final DiscountApiUrls discountApiUrls;
    private DiscountPresenter discountPresenter;
    private DiscountOnOrderPresenter discountOnOrderPresenter;

    public void setDiscountPresenter(DiscountPresenter discountPresenter) {
        this.discountPresenter = discountPresenter;
    }

    public void setDiscountOnOrderPresenter(DiscountOnOrderPresenter discountOnOrderPresenter) {
        this.discountOnOrderPresenter = discountOnOrderPresenter;
    }

    static {
        discountApiUrls = RetrofitClient.getClient().create(DiscountApiUrls.class);
    }

    public void availableDiscount(String did, String mail, String auth, String codeQR) {
        discountApiUrls.availableDiscount(did, Constants.DEVICE_TYPE, mail, auth, codeQR).enqueue(new Callback<JsonDiscountList>() {
            @Override
            public void onResponse(@NonNull Call<JsonDiscountList> call, @NonNull Response<JsonDiscountList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp availableDiscount", String.valueOf(response.body()));
                        discountPresenter.discountResponse(response.body());
                    } else {
                        Log.e("error availableDiscount", "Error availableDiscount" + response.body().getError());
                        discountPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        discountPresenter.authenticationFailure();
                    } else {
                        discountPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonDiscountList> call, @NonNull Throwable t) {
                Log.e("Fail availableDiscount ", t.getLocalizedMessage(), t);
                discountPresenter.responseErrorPresenter(null);
            }
        });
    }

    public void apply(String did, String mail, String auth, DiscountOnOrder discountOnOrder) {
        discountApiUrls.apply(did, Constants.DEVICE_TYPE, mail, auth, discountOnOrder).enqueue(new Callback<JsonPurchaseOrder>() {
            @Override
            public void onResponse(@NonNull Call<JsonPurchaseOrder> call, @NonNull Response<JsonPurchaseOrder> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp applyDiscount", String.valueOf(response.body()));
                        discountOnOrderPresenter.discountOnOrderResponse(response.body());
                    } else {
                        Log.e("error applyDiscount", "Error applyDiscount" + response.body().getError());
                        discountOnOrderPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        discountOnOrderPresenter.authenticationFailure();
                    } else {
                        discountOnOrderPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonPurchaseOrder> call, @NonNull Throwable t) {
                Log.e("Fail applyDiscount ", t.getLocalizedMessage(), t);
                discountOnOrderPresenter.responseErrorPresenter(null);
            }
        });
    }
}
