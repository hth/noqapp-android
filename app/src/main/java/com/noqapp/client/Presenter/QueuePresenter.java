package com.noqapp.client.Presenter;

import com.noqapp.client.Presenter.Beans.JsonQueue;

/**
 * Created by omkar on 3/26/17.
 */

public interface QueuePresenter {

    void didQRCodeResponse(JsonQueue queue);

    void didQRCodeError();

}
