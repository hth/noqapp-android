package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.merchant.presenter.beans.JsonTopicList;


public interface TopicPresenter {
    void queueResponse(JsonTopicList token);

    void queueError();

    void authenticationFailure(int errorCode);
}
