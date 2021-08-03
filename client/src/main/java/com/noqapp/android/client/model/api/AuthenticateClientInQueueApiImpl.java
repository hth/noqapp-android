package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.AuthenticateClientInQueueApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ClientInQueuePresenter;
import com.noqapp.android.client.presenter.beans.JsonInQueuePerson;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticateClientInQueueApiImpl {

    private static final String TAG = AuthenticateClientInQueueApiImpl.class.getSimpleName();
    private static final AuthenticateClientInQueueApi AUTHENTICATE_CLIENT_IN_QUEUE_API;
    private ClientInQueuePresenter clientInQueuePresenter;

    static {
        AUTHENTICATE_CLIENT_IN_QUEUE_API = RetrofitClient.getClient().create(AuthenticateClientInQueueApi.class);
    }

    public AuthenticateClientInQueueApiImpl(ClientInQueuePresenter clientInQueuePresenter) {
        this.clientInQueuePresenter = clientInQueuePresenter;
    }

    public void clientInQueue(String did, String mail, String auth, String codeQR, String token) {
        AUTHENTICATE_CLIENT_IN_QUEUE_API.clientInQueue(did, Constants.DEVICE_TYPE, mail, auth, codeQR, token).enqueue(new Callback<JsonInQueuePerson>() {
            @Override
            public void onResponse(@NonNull Call<JsonInQueuePerson> call, @NonNull Response<JsonInQueuePerson> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response clientInQueue", String.valueOf(response.body()));
                        clientInQueuePresenter.clientInQueueResponse(response.body());
                    } else {
                        Log.e(TAG, "Error clientInQueue" + response.body().getError());
                        clientInQueuePresenter.clientInQueueErrorPresenter(response.body().getError());
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
            public void onFailure(@NonNull Call<JsonInQueuePerson> call, @NonNull Throwable t) {
                Log.e("Failure clientInQueue", t.getLocalizedMessage(), t);
                clientInQueuePresenter.responseErrorPresenter(null);
            }
        });
    }
}
