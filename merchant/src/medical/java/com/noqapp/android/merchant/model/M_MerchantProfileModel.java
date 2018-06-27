package com.noqapp.android.merchant.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.common.beans.JsonResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/22/17 11:53 AM
 */
public class M_MerchantProfileModel extends MerchantProfileModel {
    private static final String TAG = M_MerchantProfileModel.class.getSimpleName();

    public static IntellisensePresenter intellisensePresenter;

    public static void uploadIntellisense(String did, String mail, String auth,JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        merchantProfileService.intellisense(did, Constants.DEVICE_TYPE, mail, auth, jsonProfessionalProfilePersonal).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.INVALID_CREDENTIAL) {
                    intellisensePresenter.authenticationFailure(response.code());
                    return;
                }
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    intellisensePresenter.intellisenseResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Failed image upload");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                intellisensePresenter.intellisenseError();
            }
        });
    }
}
