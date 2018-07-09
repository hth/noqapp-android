package com.noqapp.android.merchant.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.merchant.model.response.api.RegisterService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.Login;
import com.noqapp.android.merchant.presenter.beans.body.Registration;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;
import com.noqapp.common.beans.JsonProfile;

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
    public ProfilePresenter profilePresenter;

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
                    profilePresenter.profileResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history" + response.body().getError());
                    profilePresenter.profileError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
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
                    profilePresenter.profileResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history" + response.body().getError());
                    profilePresenter.profileError(response.body().getError().getReason());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }
}
