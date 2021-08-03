package com.noqapp.android.client.model.open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.ProfessionalProfile;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.QueueManagerPresenter;
import com.noqapp.android.client.presenter.beans.JsonProfessionalProfile;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

public class ProfessionalProfileImpl {
    private final String TAG = ProfessionalProfileImpl.class.getSimpleName();
    private static final ProfessionalProfile PROFESSIONAL_PROFILE;
    private QueueManagerPresenter queueManagerPresenter;

    public ProfessionalProfileImpl(QueueManagerPresenter queueManagerPresenter) {
        this.queueManagerPresenter = queueManagerPresenter;
    }

    static {
        PROFESSIONAL_PROFILE = RetrofitClient.getClient().create(ProfessionalProfile.class);
    }

    /**
     * @param did
     * @param webProfileId
     */
    public void profile(String did, String webProfileId) {
        PROFESSIONAL_PROFILE.profile(did, DEVICE_TYPE, webProfileId).enqueue(new Callback<JsonProfessionalProfile>() {
            @Override
            public void onResponse(@NonNull Call<JsonProfessionalProfile> call, @NonNull Response<JsonProfessionalProfile> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d(TAG, "QueueManagerProfile " + response.body());
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
                Log.e(TAG, "Professional Profile fail " + t.getLocalizedMessage(), t);
                queueManagerPresenter.responseErrorPresenter(null);
            }
        });
    }
}
