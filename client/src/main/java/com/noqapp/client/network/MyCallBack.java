package com.noqapp.client.network;



import android.util.Log;

import com.noqapp.client.views.activities.LaunchActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chandra on 4/22/17.
 */

public class MyCallBack<T> implements Callback<T> {
    private static final String TAG = MyCallBack.class.getSimpleName();

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (response.code() == 404) {
            Log.e(TAG, "404 Not found link=" + response.raw().request().url());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }
}