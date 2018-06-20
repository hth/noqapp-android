package com.noqapp.android.client.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.noqapp.android.client.model.response.api.DependentApiService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.DependencyPresenter;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.common.beans.JsonProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public final class DependencyModel {
    private static final String TAG = DependencyModel.class.getSimpleName();

    private static final DependentApiService dependentApiService;
    public static DependencyPresenter dependencyPresenter;

    static {
        dependentApiService = RetrofitClient.getClient().create(DependentApiService.class);
    }

    /**
     * @param registration
     */
    public static void addDependency(String did, String mail, String auth,  Registration registration) {
        dependentApiService.add(did, Constants.DEVICE_TYPE, mail, auth, registration).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (null != response.body()) {
                    Log.d("Response", String.valueOf(response.body()));
                    dependencyPresenter.dependencyResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history" + response.body().getError());
                    dependencyPresenter.dependencyError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                dependencyPresenter.dependencyError();
            }
        });
    }
}
