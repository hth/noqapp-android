package com.noqapp.android.merchant.model;

import com.noqapp.android.merchant.model.response.api.LoginService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.LoginPresenter;

import android.support.annotation.NonNull;
import android.util.Log;
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
    private LoginPresenter loginPresenter;

    public LoginModel(LoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
    }

    static {
        loginService = RetrofitClient.getClient().create(LoginService.class);
    }

    /**
     * @param mail
     * @param password
     */
    public void login(String mail, String password) {
        loginService.login(mail, password).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
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
