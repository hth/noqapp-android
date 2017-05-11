package com.noqapp.client.presenter;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * User: hitender
 * Date: 4/1/17 3:30 PM
 */

public interface TokenAndQueuePresenter {
    void queueResponse(List<JsonTokenAndQueue> tokenAndQueues);

    void queueError();

    void noCurrentQueue();

    void noHistoryQueue();
}
