package com.noqapp.merchant.model;

import android.util.Log;

import com.noqapp.merchant.model.response.api.LoginService;
import com.noqapp.merchant.model.response.api.MerchantProfileService;
import com.noqapp.merchant.network.RetrofitClient;
import com.noqapp.merchant.presenter.beans.JsonMerchant;
import com.noqapp.merchant.presenter.beans.body.Authenticate;

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

    static {
        loginService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(LoginService.class);
    }

    /**
     *
     * @param authenticate
     */
    public static void login(Authenticate authenticate) {
        loginService.login(authenticate).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    Log.d("Response Mail", String.valueOf(response.headers().get(APIConstant.key.XR_MAIL)));
                    Log.d("Response Auth", String.valueOf(response.headers().get(APIConstant.key.XR_AUTH)));

                    //TODO success response ???
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
