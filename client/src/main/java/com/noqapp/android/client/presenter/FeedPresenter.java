package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonFeedList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface FeedPresenter extends ResponseErrorPresenter {

    void allActiveFeedResponse(JsonFeedList jsonFeedList);
}

