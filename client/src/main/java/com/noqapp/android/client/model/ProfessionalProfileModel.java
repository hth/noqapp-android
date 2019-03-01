package com.noqapp.android.client.model;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

import com.noqapp.android.client.model.response.open.ProfessionalProfileService;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.QueueManagerPresenter;
import com.noqapp.android.client.presenter.beans.JsonProfessionalProfile;
import com.noqapp.android.client.utils.Constants;

import androidx.annotation.NonNull;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessionalProfileModel {

    private final String TAG = ProfessionalProfileModel.class.getSimpleName();
    private static final ProfessionalProfileService professionalProfileService;
    private QueueManagerPresenter queueManagerPresenter;

    public ProfessionalProfileModel(QueueManagerPresenter queueManagerPresenter) {
        this.queueManagerPresenter = queueManagerPresenter;
    }

    static {
        professionalProfileService = RetrofitClient.getClient().create(ProfessionalProfileService.class);
    }

    /**
     * @param did
     * @param webProfileId
     */
    public void profile(String did, String webProfileId) {
        professionalProfileService.profile(did, DEVICE_TYPE, webProfileId).enqueue(new Callback<JsonProfessionalProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfessionalProfile> call, @NonNull Response<JsonProfessionalProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("QueueManagerProfile", String.valueOf(response.body()));
                        queueManagerPresenter.queueManagerResponse(response.body());
                    } else {
                        Log.e(TAG, "Empty QueueManagerProfile");
                        queueManagerPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        queueManagerPresenter.authenticationFailure();
                    } else {
                        queueManagerPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonProfessionalProfile> call, @NonNull Throwable t) {
                Log.e("professionalProfilefail", t.getLocalizedMessage(), t);
                queueManagerPresenter.responseErrorPresenter(null);
            }
        });
    }
}
