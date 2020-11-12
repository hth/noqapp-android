package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.AuthenticateClientInQueueApiUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ClientInQueuePresenter;
import com.noqapp.android.client.presenter.beans.JsonInQueuePerson;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticateClientInQueueApiCalls {

    private static final String TAG = AuthenticateClientInQueueApiCalls.class.getSimpleName();
    private static final AuthenticateClientInQueueApiUrls authenticateClientInQueueApiUrls;
    private ClientInQueuePresenter clientInQueuePresenter;

    static {
        authenticateClientInQueueApiUrls = RetrofitClient.getClient().create(AuthenticateClientInQueueApiUrls.class);
    }

    public AuthenticateClientInQueueApiCalls(ClientInQueuePresenter clientInQueuePresenter) {
        this.clientInQueuePresenter = clientInQueuePresenter;
    }
    
    public void clientInQueue(String did, String mail, String auth, String codeQR, String token) {
        authenticateClientInQueueApiUrls.clientInQueue(did, Constants.DEVICE_TYPE, mail, auth, codeQR, token).enqueue(new Callback<JsonInQueuePerson>() {
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
