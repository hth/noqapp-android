package com.noqapp.android.client.model.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.noqapp.android.client.model.response.open.Feed;
import com.noqapp.android.client.network.RetrofitClient;
import com.noqapp.android.client.presenter.FeedPresenter;
import com.noqapp.android.client.presenter.beans.JsonFeedList;
import com.noqapp.android.client.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.noqapp.android.client.utils.Constants.DEVICE_TYPE;

public class FeedImpl {

    private final String TAG = FeedImpl.class.getSimpleName();
    private static final Feed FEED;
    private FeedPresenter feedPresenter;

    public FeedImpl(FeedPresenter feedPresenter) {
        this.feedPresenter = feedPresenter;
    }

    static {
        FEED = RetrofitClient.getClient().create(Feed.class);
    }


    public void activeFeed(String did) {
        FEED.activeFeed(did, DEVICE_TYPE).enqueue(new Callback<JsonFeedList>() {
            @Override
            public void onResponse(@NonNull Call<JsonFeedList> call, @NonNull Response<JsonFeedList> response) {
                if (response.code() == Constants.SERVER_RESPONSE_CODE_SUCCESS) {
                    if (null != response.body() && null == response.body().getError()) {
                        Log.d("Response activeFeed", String.valueOf(response.body()));
                        feedPresenter.allActiveFeedResponse(response.body());
                    } else {
                        Log.e(TAG, "Error activeFeed " + response.body().getError());
                        feedPresenter.responseErrorPresenter(response.body().getError());
                    }
                } else {
                    if (response.code() == Constants.INVALID_CREDENTIAL) {
                        feedPresenter.authenticationFailure();
                    } else {
                        feedPresenter.responseErrorPresenter(response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonFeedList> call, @NonNull Throwable t) {
                Log.e("Failure activeFeed ", t.getLocalizedMessage(), t);
                feedPresenter.responseErrorPresenter(null);
            }
        });
    }
}
