package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.DependentApi;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.DependencyPresenter;
import com.noqapp.android.client.presenter.beans.body.Registration;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DependentApiCall {
    private final String TAG = DependentApiCall.class.getSimpleName();
    private static final DependentApi DEPENDENT_API;
    private DependencyPresenter dependencyPresenter;

    public DependentApiCall(DependencyPresenter dependencyPresenter) {
        this.dependencyPresenter = dependencyPresenter;
    }

    static {
        DEPENDENT_API = RetrofitClient.getClient().create(DependentApi.class);
    }

    /**
     * @param registration
     */
    public void addDependency(String did, String mail, String auth,  Registration registration) {
        DEPENDENT_API.add(did, Constants.DEVICE_TYPE, mail, auth, registration).enqueue(new Callback<JsonProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfile> call, @NonNull Response<JsonProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("addDependency", String.valueOf(response.body()));
                        dependencyPresenter.dependencyResponse(response.body());
                    } else {
                        Log.e(TAG, "addDependency error" + response.body().getError());
                        dependencyPresenter.responseErrorPresenter(response.body().getError());
                    }
                }else{
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        dependencyPresenter.authenticationFailure();
                    } else {
                        dependencyPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfile> call, @NonNull Throwable t) {
                Log.e("addDependency failed", t.getLocalizedMessage(), t);
                dependencyPresenter.responseErrorPresenter(null);
            }
        });
    }
}
