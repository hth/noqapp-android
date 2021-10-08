package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.ClientPreferenceApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ClientPreferencePresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonUserPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientPreferenceApiImpl {
    private final String TAG = ClientPreferenceApiImpl.class.getSimpleName();
    private static final ClientPreferenceApi CLIENT_PREFERENCE_API;
    private ClientPreferencePresenter clientPreferencePresenter;

    public void setClientPreferencePresenter(ClientPreferencePresenter clientPreferencePresenter) {
        this.clientPreferencePresenter = clientPreferencePresenter;
    }

    static {
        CLIENT_PREFERENCE_API = RetrofitClient.getClient().create(ClientPreferenceApi.class);
    }

    public void notificationSound(String did, String mail, String auth) {
        CLIENT_PREFERENCE_API.notificationSound(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonUserPreference>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserPreference> call, @NonNull Response<JsonUserPreference> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        clientPreferencePresenter.clientPreferencePresenterResponse(response.body());
                        Log.d("notificationSound", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty notificationSound");
                        clientPreferencePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        clientPreferencePresenter.authenticationFailure();
                    } else {
                        clientPreferencePresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserPreference> call, @NonNull Throwable t) {
                Log.e("onFail notificatiSound", t.getLocalizedMessage(), t);
                clientPreferencePresenter.responseErrorPresenter(null);
            }
        });
    }

    public void promotionalSMS(String did, String mail, String auth) {
        CLIENT_PREFERENCE_API.promotionalSMS(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonUserPreference>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserPreference> call, @NonNull Response<JsonUserPreference> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        clientPreferencePresenter.clientPreferencePresenterResponse(response.body());
                        Log.d("promotionalSMS", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty promotionalSMS");
                        clientPreferencePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        clientPreferencePresenter.authenticationFailure();
                    } else {
                        clientPreferencePresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserPreference> call, @NonNull Throwable t) {
                Log.e("onFail promotionalSMS", t.getLocalizedMessage(), t);
                clientPreferencePresenter.responseErrorPresenter(null);
            }
        });
    }

    public void order(String did, String mail, String auth, JsonUserPreference jsonUserPreference) {
        CLIENT_PREFERENCE_API.order(did, Constants.DEVICE_TYPE, mail, auth, jsonUserPreference).enqueue(new Callback<JsonUserPreference>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserPreference> call, @NonNull Response<JsonUserPreference> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        clientPreferencePresenter.clientPreferencePresenterResponse(response.body());
                        Log.e("order address", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty order address");
                        clientPreferencePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        clientPreferencePresenter.authenticationFailure();
                    } else {
                        clientPreferencePresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserPreference> call, @NonNull Throwable t) {
                Log.e("onFail address", t.getLocalizedMessage(), t);
                clientPreferencePresenter.responseErrorPresenter(null);
            }
        });
    }
}
