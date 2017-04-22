package com.noqapp.merchant.model;

import android.util.Log;

import com.noqapp.merchant.model.response.api.ManageQueueService;
import com.noqapp.merchant.network.RetrofitClient;
import com.noqapp.merchant.presenter.beans.JsonTopic;
import com.noqapp.merchant.presenter.beans.JsonTopicList;
import com.noqapp.merchant.utils.Constants;

import java.util.List;

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
        manageQueueService.getQueues(did, DEVICE_TYPE, mail, auth).enqueue(new Callback<JsonTopicList>() {
            @Override
            public void onResponse(Call<JsonTopicList> call, Response<JsonTopicList> response) {
                if (response.body() != null) {
                    Log.d("Response", String.valueOf(response.body()));
                } else {
                    //TODO something logical
                    Log.e(TAG, "Empty history");
                }
            }

            @Override
            public void onFailure(Call<JsonTopicList> call, Throwable t) {
                Log.e("Response", t.getLocalizedMessage(), t);
            }
        });
    }
}
