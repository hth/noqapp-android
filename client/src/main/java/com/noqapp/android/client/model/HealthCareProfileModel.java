package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.open.HealthCareProfileService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.QueueManagerPresenter;
import com.noqapp.android.client.presenter.beans.JsonHealthCareProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

public class HealthCareProfileModel {

    private static final String TAG = HealthCareProfileModel.class.getSimpleName();

    private static final HealthCareProfileService healthCareProfileService;
    public static QueueManagerPresenter queueManagerPresenter;

    static {
        healthCareProfileService = RetrofitClient.getClient().create(HealthCareProfileService.class);
    }

    /**
     * @param did
     * @param webProfileId
     */
    public static void getQueueManagerProfile(String did, String webProfileId) {
        healthCareProfileService.getQueueManagerProfile(did, DEVICE_TYPE, webProfileId).enqueue(new Callback<JsonHealthCareProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonHealthCareProfile> call, @NonNull Response<JsonHealthCareProfile> response) {
                if (response.body() != null) {
                    Log.d("QueueManagerProfile", String.valueOf(response.body()));
                    queueManagerPresenter.queueManagerResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonHealthCareProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                queueManagerPresenter.queueManagerError();
            }
        });
    }
}
