package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.merchant.model.response.api.ReceiptApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ReceiptInfoPresenter;
import com.noqapp.android.merchant.views.pojos.Receipt;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ReceiptInfoApiCalls {
    private static final String TAG = ReceiptInfoApiCalls.class.getSimpleName();

    private static final ReceiptApiUrls registerApiUrls;
    private ReceiptInfoPresenter receiptInfoPresenter;

    public void setReceiptInfoPresenter(ReceiptInfoPresenter receiptInfoPresenter) {
        this.receiptInfoPresenter = receiptInfoPresenter;
    }

    static {
        registerApiUrls = RetrofitClient.getClient().create(ReceiptApiUrls.class);
    }

    public void detail(String did, String mail, String auth, Receipt receipt) {
        registerApiUrls.detail(did, Constants.DEVICE_TYPE, mail, auth, receipt).enqueue(new Callback<Receipt>() {
            @Override
            public void onResponse(@NonNull Call<Receipt> call, @NonNull Response<Receipt> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("receipt detail", String.valueOf(response.body()));
                        receiptInfoPresenter.receiptInfoResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed receipt detail");
                        receiptInfoPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        receiptInfoPresenter.authenticationFailure();
                    } else {
                        receiptInfoPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Receipt> call, @NonNull Throwable t) {
                Log.e("onFailureReceipt detail", t.getLocalizedMessage(), t);
                receiptInfoPresenter.responseErrorPresenter(null);
            }
        });
    }
}
