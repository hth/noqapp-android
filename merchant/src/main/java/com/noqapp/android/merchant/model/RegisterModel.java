package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.merchant.model.response.api.RegisterService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.body.Login;
import com.noqapp.android.merchant.presenter.beans.body.Registration;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ProfilePresenter;

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
    private static final String TAG = RegisterModel.class.getSimpleName();

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
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response register", String.valueOf(response.body()));
                        profilePresenter.profileResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                                response.headers().get(APIConstant.Key.XR_AUTH));
                    } else {
                        Log.e(TAG, "error register" + response.body().getError());
                        profilePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        profilePresenter.authenticationFailure();
                    } else {
                        profilePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("fail register", t.getLocalizedMessage(), t);
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
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response login", String.valueOf(response.body()));
                        profilePresenter.profileResponse(response.body(), response.headers().get(APIConstant.Key.XR_MAIL),
                                response.headers().get(APIConstant.Key.XR_AUTH));
                    } else {
                        Log.e(TAG, "Error login" + response.body().getError());
                        profilePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        profilePresenter.authenticationFailure();
                    } else {
                        profilePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("fail login", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }
}
