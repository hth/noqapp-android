package com.noqapp.android.merchant.model;

import android.util.Log;

import com.noqapp.android.merchant.model.response.api.ManageQueueService;
import com.noqapp.android.merchant.network.RetrofitClient;
import com.noqapp.android.merchant.presenter.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;
import com.noqapp.android.merchant.presenter.beans.body.Served;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.views.interfaces.TopicPresenter;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        manageQueueService = RetrofitClient.getClient().create(ManageQueueService.class);
    }

    /**
     * @param did
     * @param mail
     * @param auth
     */
    public static void getQueues(String did, String mail, String auth) {
        manageQueueService.getQueues(did, Constants.DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonTopicList>() {
            @Override
            public void onResponse(Call<JsonTopicList> call, Response<JsonTopicList> response) {
                if (null != response.body() && null == response.body().getError()) {
                    Log.d("Get all assigned queues", String.valueOf(response.body()));
                    topicPresenter.queueResponse(response.body());
                } else {
                    //TODO something logical
                    Log.e(TAG, "Found error while getting all queues assigned");
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
        manageQueueService.served(did, Constants.DEVICE_TYPE, mail, auth, served).enqueue(new Callback<JsonToken>() {
            @Override
            public void onResponse(Call<JsonToken> call, Response<JsonToken> response) {
                if (response.body() != null && response.body().getError() == null) {
                    if (StringUtils.isNotBlank(response.body().getCodeQR())) {
                        Log.d(TAG, "After clicking Next, response jsonToken" + response.body().toString());
                        manageQueuePresenter.manageQueueResponse(response.body());
                    } else {
                        //TODO something logical
                        Log.e(TAG, "Failed to get response");
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
