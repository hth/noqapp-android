package com.noqapp.android.client.model.open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.APIConstant;
import com.noqapp.android.client.model.response.open.AccountClient;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.Login;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/8/17 8:38 PM
 */

public class AccountClientImpl {
    private final String TAG = AccountClientImpl.class.getSimpleName();
    private static final com.noqapp.android.client.model.response.open.AccountClient ACCOUNT_CLIENT;
    private ProfilePresenter profilePresenter;

    public AccountClientImpl(ProfilePresenter profilePresenter) {
        this.profilePresenter = profilePresenter;
    }

    static {
        ACCOUNT_CLIENT = RetrofitClient.getClient().create(AccountClient.class);
    }

    /**
     * @param registration
     */
    public void register(String did, Registration registration) {
        ACCOUNT_CLIENT.register(did, Constants.DEVICE_TYPE, registration).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response register", String.valueOf(response.body()));
                        profilePresenter.profileResponse(
                            response.body(),
                            response.headers().get(APIConstant.Key.XR_MAIL),
                            response.headers().get(APIConstant.Key.XR_AUTH));
                    } else {
                        Log.e(TAG, "Error register:" + response.body().getError());
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
                Log.e("Failure register", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }

    /**
     * @param login
     */
    public void login(String did, Login login) {
        ACCOUNT_CLIENT.login(did, Constants.DEVICE_TYPE, login).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response login", String.valueOf(response.body()));
                        profilePresenter.profileResponse(
                                response.body(),
                                response.headers().get(APIConstant.Key.XR_MAIL),
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
                Log.e("Failure login", t.getLocalizedMessage(), t);
                profilePresenter.profileError();
            }
        });
    }
}
