package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.KioskApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.body.JoinQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Authorised call required authorised user
 */
public class KioskApiImpl {
    private final String TAG = KioskApiImpl.class.getSimpleName();
    private final static KioskApi KIOSK_API;
    private TokenPresenter tokenPresenter;

    public void setTokenPresenter(TokenPresenter tokenPresenter) {
        this.tokenPresenter = tokenPresenter;
    }

    static {
        KIOSK_API = RetrofitClient.getClient().create(KioskApi.class);
    }

    public void joinQueue(String did, String mail, String auth, JoinQueue joinQueue) {
        KIOSK_API.joinQueue(did, Constants.DEVICE_TYPE, mail, auth, joinQueue).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(@NonNull Call<JsonToken> call, @NonNull Response<JsonToken> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Resp kiosk joinQueue", response.body().toString());
                        tokenPresenter.tokenPresenterResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed to kiosk join queue" + response.body().getError());
                        tokenPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        tokenPresenter.authenticationFailure();
                    } else {
                        tokenPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonToken> call, @NonNull Throwable t) {
                Log.e("Failure kiosk joinQueue", t.getLocalizedMessage(), t);
                tokenPresenter.responseErrorPresenter(null);
            }
        });
    }
}
