package com.noqapp.merchant.network;

import com.noqapp.merchant.views.activities.LaunchActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chandra on 4/22/17.
 */

public class MyCallBack<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }


}