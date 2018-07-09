package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.merchant.presenter.beans.JsonTopicList;


public interface TopicPresenter {
    void topicPresenterResponse(JsonTopicList token);

    void topicPresenterError();

    void authenticationFailure(int errorCode);
}
