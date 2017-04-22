package com.noqapp.client.presenter;

import com.noqapp.client.presenter.beans.JsonToken;

/**
 * User: hitender
 * Date: 4/1/17 12:22 PM
 */

public interface TokenPresenter {
    void queueResponse(JsonToken token);

    void queueError();
}
