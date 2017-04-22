package com.noqapp.client.presenter;

import com.noqapp.client.presenter.beans.JsonQueue;

/**
 * User: omkar
 * Date: 3/26/17 4:27 PM
 */
public interface QueuePresenter {

    void queueResponse(JsonQueue queue);

    void queueError();

}
