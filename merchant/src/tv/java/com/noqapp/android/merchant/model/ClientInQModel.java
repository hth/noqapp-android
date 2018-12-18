package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.api.ClientInQueueService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.ClientInQPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;
import com.noqapp.android.merchant.utils.Constants;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientInQModel {

    private static final String TAG = MerchantProfileModel.class.getSimpleName();

    protected static final ClientInQueueService clientInQueueService;
    private ClientInQPresenter clientInQPresenter;

    public ClientInQModel(ClientInQPresenter clientInQPresenter) {
        this.clientInQPresenter = clientInQPresenter;
    }

    static {
        clientInQueueService = RetrofitClient.getClient().create(ClientInQueueService.class);
    }

    /**
     * @param mail
     * @param auth
     */
    public void toBeServedClients(String did, String mail, String auth, QueueDetail queueDetail) {
        clientInQueueService.toBeServedClients(did, Constants.DEVICE_TYPE, mail, auth, queueDetail).enqueue(new Callback<JsonQueueTVList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueueTVList> call, @NonNull Response<JsonQueueTVList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        clientInQPresenter.ClientInResponse(response.body());
                        Log.d("toBeServedClients", String.valueOf(response.body()));
                    } else {
                        Log.e(TAG, "Empty toBeServedClients");
                        clientInQPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        clientInQPresenter.authenticationFailure();
                    } else {
                        clientInQPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueueTVList> call, @NonNull Throwable t) {
                Log.e("onFailure toBeServedCli", t.getLocalizedMessage(), t);
                clientInQPresenter.responseErrorPresenter(null);
            }
        });
    }
}
