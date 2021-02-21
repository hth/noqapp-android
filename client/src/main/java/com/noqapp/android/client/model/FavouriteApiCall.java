package com.noqapp.android.client.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.api.FavouriteAPIUrls;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.beans.FavoriteElastic;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.FavouritePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteApiCall {
    private final String TAG = FavouriteApiCall.class.getSimpleName();
    private static final FavouriteAPIUrls favouriteAPIUrls;
    private FavouritePresenter favouritePresenter;

    public FavouriteApiCall(FavouritePresenter favouritePresenter) {
        this.favouritePresenter = favouritePresenter;
    }

    static {
        favouriteAPIUrls = RetrofitClient.getClient().create(FavouriteAPIUrls.class);
    }

    public void actionOnFavorite(String did, String mail, String auth, FavoriteElastic favoriteElastic) {
        favouriteAPIUrls.actionOnFavorite(did, Constants.DEVICE_TYPE, mail, auth, favoriteElastic).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response favourite", String.valueOf(response.body()));
                        favouritePresenter.favouriteResponse(response.body());
                    } else {
                        Log.e(TAG, "Error favourite" + response.body().getError());
                        favouritePresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        favouritePresenter.authenticationFailure();
                    } else {
                        favouritePresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Log.e("Failure favourite", t.getLocalizedMessage(), t);
                favouritePresenter.responseErrorPresenter(null);
            }
        });
    }
}
