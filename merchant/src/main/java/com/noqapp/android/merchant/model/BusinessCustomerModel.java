package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.response.api.BusinessCustomerService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/16/17 5:59 PM
 */
public class BusinessCustomerModel {
    private static final String TAG = BusinessCustomerModel.class.getSimpleName();

    private static final BusinessCustomerService businessCustomerService;
    private QueuePersonListPresenter queuePersonListPresenter;

    public BusinessCustomerModel(QueuePersonListPresenter queuePersonListPresenter) {
        this.queuePersonListPresenter = queuePersonListPresenter;
    }

    static {
        businessCustomerService = RetrofitClient.getClient().create(BusinessCustomerService.class);
    }

    public void addId(String did, String mail, String auth, JsonBusinessCustomer jsonBusinessCustomer) {
        businessCustomerService.addId(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomer).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("addId", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while addId");
                        queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queuePersonListPresenter.authenticationFailure();
                    } else {
                        queuePersonListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("addId", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }


    public void editId(String did, String mail, String auth, JsonBusinessCustomer jsonBusinessCustomer) {
        businessCustomerService.editId(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomer).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("editId", String.valueOf(response.body()));
                        queuePersonListPresenter.queuePersonListResponse(response.body());
                    } else {
                        Log.e(TAG, "Found error while editId");
                        queuePersonListPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queuePersonListPresenter.authenticationFailure();
                    } else {
                        queuePersonListPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("editId", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }
}
