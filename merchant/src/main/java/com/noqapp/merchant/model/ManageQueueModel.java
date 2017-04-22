package com.noqapp.merchant.model;

import android.util.Log;

import com.noqapp.merchant.model.response.api.ManageQueueService;
import com.noqapp.merchant.network.MyCallBack;
import com.noqapp.merchant.network.RetrofitClient;
import com.noqapp.merchant.presenter.beans.JsonToken;
import com.noqapp.merchant.presenter.beans.JsonTopicList;
import com.noqapp.merchant.views.interfaces.TopicPresenter;

import retrofit2.Call;
import retrofit2.Response;

import static com.noqapp.merchant.utils.Constants.DEVICE_TYPE;

/**
 * User: hitender
 * Date: 4/16/17 5:59 PM
 */

public class ManageQueueModel {
    private static final String TAG = ManageQueueModel.class.getSimpleName();

    private static final ManageQueueService manageQueueService;
    public static TopicPresenter topicPresenter;
    static {
        manageQueueService = RetrofitClient.getClient(RetrofitClient.BaseURL).create(ManageQueueService.class);
    }

    /**
     *
     * @param did
     * @param mail
     * @param auth
     */
    public static void getQueues(String did, String mail, String auth) {
        manageQueueService.getQueues(did, DEVICE_TYPE, mail, auth).enqueue(new MyCallBack<JsonTopicList>() {
            @Override
            public void onResponse(Call<JsonTopicList> call, Response<JsonTopicList> response) {
                super.onResponse(call,response);
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
                super.onFailure(call,t);
                Log.e("Response", t.getLocalizedMessage(), t);
                topicPresenter.queueError();
            }
        });
    }

    /**
     *
     * @param did
     * @param mail
     * @param auth
     */
    public static void served(String did, String mail, String auth) {
        manageQueueService.served(did, DEVICE_TYPE, mail, auth).enqueue(new MyCallBack<JsonToken>() {
            @Override
            public void onResponse(Call<JsonToken> call, Response<JsonToken> response) {
                super.onResponse(call,response);
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonToken> call, Throwable t) {
                super.onFailure(call,t);
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
