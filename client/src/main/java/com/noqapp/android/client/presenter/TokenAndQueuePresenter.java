package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * User: hitender
 * Date: 4/1/17 3:30 PM
 */

public interface TokenAndQueuePresenter {

    void currentQueueResponse(List<JsonTokenAndQueue> tokenAndQueues);

    void historyQueueResponse(List<JsonTokenAndQueue> tokenAndQueues);

    void historyQueueError();

    void currentQueueError();

}
