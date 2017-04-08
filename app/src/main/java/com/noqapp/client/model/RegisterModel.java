package com.noqapp.client.model;

import android.util.Log;

import com.noqapp.client.model.response.open.RegisterService;
import com.noqapp.client.network.RetrofitClient;
import com.noqapp.client.presenter.ProfilePresenter;
import com.noqapp.client.presenter.beans.JsonQueue;
import com.noqapp.client.presenter.beans.Profile;
import com.noqapp.client.presenter.beans.body.Registration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.client.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 4/8/17 8:38 PM
 */

public final class RegisterModel {
    private static final RegisterService registerService;
    public static ProfilePresenter profilePresenter;

    static {
        registerService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(RegisterService.class);
    }

    /**
     *
     * @param registration
     */
    public static void register(Registration registration) {
        registerService.register(registration).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("Response", String.valueOf(response.body()));
                profilePresenter.queueResponse(response.body());
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.queueError();
            }
        });
    }
}
