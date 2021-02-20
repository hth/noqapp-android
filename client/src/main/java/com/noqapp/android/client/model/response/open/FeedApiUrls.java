package com.noqapp.android.client.model.response.open;

import com.noqapp.android.client.presenter.beans.JsonFeedList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * hitender
 * 2018-12-07 12:12
 */
public interface FeedApiUrls {
    /**
     * ERROR
     * {@link com.noqapp.android.client.presenter.beans.JsonFeedList#jsonFeeds} is empty when not found
     */
    @GET("open/feed/v1/active")
    Call<JsonFeedList> activeFeed(
            @Header("X-R-DID")
            String did,

            @Header("X-R-DT")
            String dt
    );
}
