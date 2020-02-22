package com.noqapp.android.merchant.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.common.beans.JsonUserPreference;
import com.noqapp.android.merchant.model.response.api.MerchantPreferenceApiUrls;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.MerchantPreferencePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantPreferenceApiCalls {
    private final String TAG = MerchantPreferenceApiCalls.class.getSimpleName();
    private static final MerchantPreferenceApiUrls merchantPreferenceApiUrls;
    private MerchantPreferencePresenter merchantPreferencePresenter;

    public void setMerchantPreferencePresenter(MerchantPreferencePresenter merchantPreferencePresenter) {
        this.merchantPreferencePresenter = merchantPreferencePresenter;
    }

    static {
        merchantPreferenceApiUrls = RetrofitClient.getClient().create(MerchantPreferenceApiUrls.class);
    }

    public void notificationSound(String did, String mail, String auth) {
        merchantPreferenceApiUrls.notificationSound(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonUserPreference>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserPreference> call, @NonNull Response<JsonUserPreference> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        merchantPreferencePresenter.merchantPreferencePresenterResponse(response.body());
                        Log.d("notificationSound", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty notificationSound");
                        merchantPreferencePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        merchantPreferencePresenter.authenticationFailure();
                    } else {
                        merchantPreferencePresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserPreference> call, @NonNull Throwable t) {
                Log.e("onFail notificatiSound", t.getLocalizedMessage(), t);
                merchantPreferencePresenter.responseErrorPresenter(null);
            }
        });
    }

    public void promotionalSMS(String did, String mail, String auth) {
        merchantPreferenceApiUrls.promotionalSMS(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonUserPreference>() {
            @Override
            public void onResponse(@NonNull Call<JsonUserPreference> call, @NonNull Response<JsonUserPreference> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        merchantPreferencePresenter.merchantPreferencePresenterResponse(response.body());
                        Log.d("promotionalSMS", String.valueOf(response.body()));
                    } else {
                        Log.d(TAG, "Empty promotionalSMS");
                        merchantPreferencePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        merchantPreferencePresenter.authenticationFailure();
                    } else {
                        merchantPreferencePresenter.responseErrorPresenter(response.code());
                        Log.e(TAG, "" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonUserPreference> call, @NonNull Throwable t) {
                Log.e("onFail promotionalSMS", t.getLocalizedMessage(), t);
                merchantPreferencePresenter.responseErrorPresenter(null);
            }
        });
    }
}
