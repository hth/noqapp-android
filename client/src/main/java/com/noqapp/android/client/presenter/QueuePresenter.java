package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface QueuePresenter extends ResponseErrorPresenter{

    void queueResponse(JsonQueue queue);

    void queueResponse(BizStoreElasticList bizStoreElasticList);

    void queueError();
}
