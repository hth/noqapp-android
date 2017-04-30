package com.noqapp.merchant.model;

import android.util.Log;

import com.noqapp.merchant.BuildConfig;
import com.noqapp.merchant.model.response.api.ManageQueueService;
import com.noqapp.merchant.network.RetrofitClient;
import com.noqapp.merchant.presenter.beans.ErrorEncounteredJson;
import com.noqapp.merchant.presenter.beans.JsonToken;
import com.noqapp.merchant.presenter.beans.JsonTopicList;
import com.noqapp.merchant.presenter.beans.body.Served;
import com.noqapp.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.merchant.views.interfaces.TopicPresenter;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.merchant.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 4/16/17 5:59 PM
 */

public class ManageQueueModel {
    private static final String TAG = ManageQueueModel.class.getSimpleName();

    private static final ManageQueueService manageQueueService;
    public static ManageQueuePresenter manageQueuePresenter;
    public static TopicPresenter topicPresenter;

    static {
        manageQueueService = RetrofitClient.getClient(BuildConfig.NOQAPP_MOBILE).create(ManageQueueService.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public static void getQueues(String did, String mail, String auth) {
        manageQueueService.getQueues(did, DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonTopicList>() {
            @Override
            public void onResponse(Call<JsonTopicList> call, Response<JsonTopicList> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                    topicPresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonTopicList> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
                topicPresenter.queueError();
            }
        });
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public static void served(String did, String mail, String auth, Served served) {
        manageQueueService.served(did, DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(Call<JsonToken> call, Response<JsonToken> response) {
                if (response.body() != null && response.body().getError() == null) {
                    if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                        Log.d("Response", String.valueOf(response.body()));
                        manageQueuePresenter.manageQueueResponse(response.body());
                    } else {
                        //TODO something logical
                        Log.e(TAG, "Failed to get token");
                    }
                } else if (response.body() != null && response.body().getError() != null) {
                    ErrorEncounteredJson errorEncounteredJson = response.body().getError();
                    Log.e(TAG, "Got error" + errorEncounteredJson.getReason());
                }
            }

            @Override
            public void onFailure(Call<JsonToken> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
