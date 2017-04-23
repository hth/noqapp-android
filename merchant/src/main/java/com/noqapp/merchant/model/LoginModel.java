package com.noqapp.merchant.model;

import android.util.Log;

import com.noqapp.merchant.model.response.api.LoginService;
import com.noqapp.merchant.network.MyCallBack;
import com.noqapp.merchant.network.RetrofitClient;
import com.noqapp.merchant.views.interfaces.LoginPresenter;

import retrofit2.Call;
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
        loginService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(LoginService.class);
    }

    /**
     *
     * @param mail
     * @param password
     */
    public static void login(String mail, String password) {
        loginService.login(mail, password).enqueue(new MyCallBack<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                super.onResponse(call,response);
                loginPresenter.loginResponse(String.valueOf(response.headers().get(APIConstant.key.XR_MAIL)),
                        String.valueOf(response.headers().get(APIConstant.key.XR_AUTH)));
                Log.d("Response", String.valueOf(response.body()));
                Log.d("Response Mail", String.valueOf(response.headers().get(APIConstant.key.XR_MAIL)));
                Log.d("Response Auth", String.valueOf(response.headers().get(APIConstant.key.XR_AUTH)));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                super.onFailure(call,t);
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
