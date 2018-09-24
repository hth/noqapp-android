package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonTopicList;


public interface TopicPresenter extends ResponseErrorPresenter{
    void topicPresenterResponse(JsonTopicList token);

    void topicPresenterError();
}
