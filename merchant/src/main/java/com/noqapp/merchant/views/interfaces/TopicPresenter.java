package com.noqapp.merchant.views.interfaces;


import com.noqapp.merchant.presenter.beans.JsonTopicList;



public interface TopicPresenter {
    void queueResponse(JsonTopicList token);

    void queueError();
}
