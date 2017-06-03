package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.open.RegisterService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.presenter.beans.body.Registration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/8/17 8:38 PM
 */

public final class RegisterModel {
    private static final String TAG = RegisterModel.class.getSimpleName();

    private static final RegisterService registerService;
    public static ProfilePresenter profilePresenter;

    static {
        registerService = RetrofitClient.getClient().create(RegisterService.class);
    }

    /**
     * @param registration
     */
    public static void register(Registration registration) {
        registerService.register(registration).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history" + response.body().getError());
                    profilePresenter.queueError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }

    /**
     * @param login
     */
    public static void login(Login login) {
        registerService.login(login).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history" + response.body().getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }
}
