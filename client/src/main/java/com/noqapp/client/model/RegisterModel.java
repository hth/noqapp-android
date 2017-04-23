package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.open.RegisterService;
import com.noqapp.client.network.MyCallBack;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.ProfilePresenter;
import com.noqapp.client.presenter.beans.JsonProfile;
import com.noqapp.client.presenter.beans.body.Login;
import com.noqapp.client.presenter.beans.body.Registration;

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
        registerService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(RegisterService.class);
    }

    /**
     * @param registration
     */
    public static void register(Registration registration) {
        registerService.register(registration).enqueue(new MyCallBack<JsonProfile>() {
            @Override
            public void onResponse(Call<JsonProfile> call, Response<JsonProfile> response) {
                super.onResponse(call,response);
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonProfile> call, Throwable t) {
                super.onFailure(call, t);
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }

    /**
     * @param login
     */
    public static void login(Login login) {
        registerService.login(login).enqueue(new MyCallBack<JsonProfile>() {
            @Override
            public void onResponse(Call<JsonProfile> call, Response<JsonProfile> response) {
                super.onResponse(call,response);
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonProfile> call, Throwable t) {
                super.onFailure(call, t);
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }
}
