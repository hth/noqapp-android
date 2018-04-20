package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;


public interface QueuePresenter {

    void queueResponse(JsonQueue queue);

    void queueResponse(BizStoreElasticList bizStoreElasticList);

    void queueError();

    void authenticationFailure(int errorCode);

}
