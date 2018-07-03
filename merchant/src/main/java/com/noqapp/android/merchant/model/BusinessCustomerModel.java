package com.noqapp.android.merchant.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.merchant.model.response.api.BusinessCustomerService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

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




    public void addId(String did, String mail, String auth, JsonBusinessCustomerLookup jsonBusinessCustomerLookup) {
        businessCustomerService.addId(did, Constants.DEVICE_TYPE, mail, auth, jsonBusinessCustomerLookup).enqueue(new Callback<JsonQueuePersonList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueuePersonList> call, @NonNull Response<JsonQueuePersonList> response) {
                if (response.code() == 401) {
                    queuePersonListPresenter.authenticationFailure(response.code());
                    return;
                }

                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get queue setting", String.valueOf(response.body()));
                    queuePersonListPresenter.queuePersonListResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while get queue setting");
                    queuePersonListPresenter.queuePersonListError();
                }
            }


            @Override
            public void onFailure(@NonNull Call<JsonQueuePersonList> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queuePersonListPresenter.queuePersonListError();
            }
        });
    }
}
