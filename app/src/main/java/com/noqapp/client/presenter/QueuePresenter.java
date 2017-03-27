package com.noqapp.client.presenter;

import com.noqapp.client.presenter.Beans.JsonQueue;

/**
 * User: omkar
 * Date: 3/26/17 4:27 PM
 */
public interface QueuePresenter {

    void didQRCodeResponse(JsonQueue queue);

    void didQRCodeError();

}
