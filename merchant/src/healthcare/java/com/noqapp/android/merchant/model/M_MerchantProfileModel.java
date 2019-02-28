package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.utils.Constants;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: hitender
 * Date: 4/22/17 11:53 AM
 */
public class M_MerchantProfileModel extends MerchantProfileModel {
    private static final String TAG = M_MerchantProfileModel.class.getSimpleName();
    private IntellisensePresenter intellisensePresenter;

    public M_MerchantProfileModel(IntellisensePresenter intellisensePresenter) {
        this.intellisensePresenter = intellisensePresenter;
    }

    public void uploadIntellisense(String did, String mail, String auth, JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        merchantProfileService.intellisense(did, Constants.DEVICE_TYPE, mail, auth, jsonProfessionalProfilePersonal).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response intellisense", String.valueOf(response.body()));
                        intellisensePresenter.intellisenseResponse(response.body());
                    } else {
                        Log.e(TAG, "Failed intellisense");
                        intellisensePresenter.responseErrorPresenter(response.body().getError());
                    }
                }else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        intellisensePresenter.authenticationFailure();
                    }else{
                        intellisensePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("`onFailure intellisense", t.getLocalizedMessage(), t);
                intellisensePresenter.intellisenseError();
            }
        });
    }
}
