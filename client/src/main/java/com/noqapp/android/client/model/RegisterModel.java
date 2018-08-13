package com.noqapp.android.client.model;

import com.noqapp.android.client.model.response.open.RegisterService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonProfile;

import android.support.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/8/17 8:38 PM
 */

public final class RegisterModel {
    private final String TAG = RegisterModel.class.getSimpleName();
    private static final RegisterService registerService;
    private ProfilePresenter profilePresenter;

    public RegisterModel(ProfilePresenter profilePresenter) {
        this.profilePresenter = profilePresenter;
    }

    static {
        registerService = RetrofitClient.getClient().create(RegisterService.class);
    }

    /**
     * @param registration
     */
    public void register(String did, Registration registration) {
        registerService.register(did, Constants.DEVICE_TYPE, registration).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
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
    public void login(String did, Login login) {
        registerService.login(did, Constants.DEVICE_TYPE, login).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    profilePresenter.queueResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history" + response.body().getError());
                    profilePresenter.queueError(response.body().getError().getReason());
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
