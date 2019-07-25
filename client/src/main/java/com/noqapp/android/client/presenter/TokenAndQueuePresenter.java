package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

import java.util.List;

/**
 * User: hitender
 * Date: 4/1/17 3:30 PM
 */
public interface TokenAndQueuePresenter extends ResponseErrorPresenter{

    void currentQueueResponse(JsonTokenAndQueueList tokenAndQueues);

    void historyQueueResponse(List<JsonTokenAndQueue> tokenAndQueues, boolean sinceBeginning);

    void historyQueueError();

    void currentQueueError();
}
