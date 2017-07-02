package com.noqapp.android.merchant.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.merchant.model.response.api.LoginService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.views.interfaces.LoginPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/22/17 1:09 PM
 */

public class LoginModel {
    private static final String TAG = LoginModel.class.getSimpleName();

    private static final LoginService loginService;
    public static LoginPresenter loginPresenter;

    static {
        loginService = RetrofitClient.getClient().create(LoginService.class);
    }

    /**
     * @param mail
     * @param password
     */
    public static void login(String mail, String password) {
        loginService.login(mail, password).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                loginPresenter.loginResponse(
                        response.headers().get(APIConstant.Key.XR_MAIL),
                        response.headers().get(APIConstant.Key.XR_AUTH));

                Log.d("Response", String.valueOf(response.body()));
                Log.d("Response Mail", String.valueOf(response.headers().get(APIConstant.Key.XR_MAIL)));
                Log.d("Response Auth", String.valueOf(response.headers().get(APIConstant.Key.XR_AUTH)));
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
