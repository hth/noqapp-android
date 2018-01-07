package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonQueueList;

/**
 * User: omkar
 * Date: 3/26/17 4:27 PM
 */
public interface QueuePresenter {

    void queueResponse(JsonQueue queue);

    void queueResponse(JsonQueueList jsonQueues);

    void queueError();

    void authenticationFailure(int errorCode);

}
