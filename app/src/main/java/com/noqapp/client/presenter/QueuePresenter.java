package com.noqapp.client.presenter;

import com.noqapp.client.presenter.Beans.JsonQueue;

/**
 * Created by omkar on 3/26/17.
 */

public interface QueuePresenter {

    void didQRCodeResponse(JsonQueue queue);

    void didQRCodeError();

}
