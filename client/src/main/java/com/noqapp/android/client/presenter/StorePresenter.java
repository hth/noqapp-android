package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonQueueList;
import com.noqapp.android.client.presenter.beans.JsonStore;

/**
 * User: omkar
 * Date: 3/26/17 4:27 PM
 */
public interface StorePresenter {

    void storeResponse(JsonStore jsonStore);

    void storeError();

    void authenticationFailure(int errorCode);

}
