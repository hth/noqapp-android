package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.model.response.api.MessageCustomerApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.merchant.MessageCustomer;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.MessageCustomerPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class MessageCustomerApiCalls {
    private static final String TAG = MessageCustomerApiCalls.class.getSimpleName();

    private static final MessageCustomerApiUrls messageCustomerApiUrls;
    private MessageCustomerPresenter messageCustomerPresenter;

    public MessageCustomerApiCalls setReceiptInfoPresenter(MessageCustomerPresenter messageCustomerPresenter) {
        this.messageCustomerPresenter = messageCustomerPresenter;
        return this;
    }

    static {
        messageCustomerApiUrls = RetrofitClient.getClient().create(MessageCustomerApiUrls.class);
    }

    public void messageCustomer(String did, String mail, String auth, MessageCustomer messageCustomer) {
        messageCustomerApiUrls.messageCustomer(did, Constants.DEVICE_TYPE, mail, auth, messageCustomer).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("messageCustomer detail", String.valueOf(response.body()));
                        messageCustomerPresenter.messageCustomerResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed messageCustomer detail");
                        messageCustomerPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        messageCustomerPresenter.authenticationFailure();
                    } else {
                        messageCustomerPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("onFailureMsgCustomer", t.getLocalizedMessage(), t);
                messageCustomerPresenter.responseErrorPresenter(null);
            }
        });
    }
}
