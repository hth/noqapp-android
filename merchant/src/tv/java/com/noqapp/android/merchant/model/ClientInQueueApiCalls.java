package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.api.ClientInQueueApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.ClientInQueuePresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;
import com.noqapp.android.merchant.utils.Constants;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientInQueueApiCalls {
    private static final String TAG = ClientInQueueApiCalls.class.getSimpleName();

    protected static final ClientInQueueApiUrls clientInQueueApiUrls;
    private ClientInQueuePresenter clientInQueuePresenter;

    public ClientInQueueApiCalls(ClientInQueuePresenter clientInQueuePresenter) {
        this.clientInQueuePresenter = clientInQueuePresenter;
    }

    static {
        clientInQueueApiUrls = RetrofitClient.getClient().create(ClientInQueueApiUrls.class);
    }

    /**
     * @param mail
     * @param auth
     */
    public void toBeServedClients(String did, String mail, String auth, QueueDetail queueDetail) {
        clientInQueueApiUrls.toBeServedClients(did, Constants.DEVICE_TYPE, mail, auth, queueDetail).enqueue(new Callback<JsonQueueTVList>() {
            @Override
            public void onResponse(@NonNull Call<JsonQueueTVList> call, @NonNull Response<JsonQueueTVList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        clientInQueuePresenter.clientInResponse(response.body());
                        Log.d("toBeServedClients", String.valueOf(response.body()));
                    } else {
                        Log.e(TAG, "Empty toBeServedClients");
                        clientInQueuePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        clientInQueuePresenter.authenticationFailure();
                    } else {
                        clientInQueuePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonQueueTVList> call, @NonNull Throwable t) {
                Log.e("onFailure toBeServedCli", t.getLocalizedMessage(), t);
                clientInQueuePresenter.responseErrorPresenter(null);
            }
        });
    }
}
