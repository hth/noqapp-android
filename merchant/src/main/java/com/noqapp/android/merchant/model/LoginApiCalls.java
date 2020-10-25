package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.merchant.model.response.open.LoginApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.LoginPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/22/17 1:09 PM
 */
public class LoginApiCalls {

    private static final LoginApiUrls loginApiUrls;
    private LoginPresenter loginPresenter;

    public LoginApiCalls(LoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
    }

    static {
        loginApiUrls = RetrofitClient.getClient().create(LoginApiUrls.class);
    }

    /**
     * @param mail
     * @param password
     */
    public void login(String mail, String password) {
        loginApiUrls.login(mail, password).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    loginPresenter.loginResponse(
                        response.headers().get(APIConstant.Key.XR_MAIL),
                        response.headers().get(APIConstant.Key.XR_AUTH));

                    Log.d("Login Response", String.valueOf(response.body()));
                    Log.d("Login Response Mail", String.valueOf(response.headers().get(APIConstant.Key.XR_MAIL)));
                    Log.d("Login Response Auth", String.valueOf(response.headers().get(APIConstant.Key.XR_AUTH)));
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        loginPresenter.authenticationFailure();
                    } else {
                        loginPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("onFailure login", t.getLocalizedMessage(), t);
                loginPresenter.loginError();
            }
        });
    }
}
