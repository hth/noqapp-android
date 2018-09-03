package com.noqapp.android.client.presenter.interfaces;

import com.noqapp.android.client.presenter.beans.JsonStore;

public interface StorePresenter {

    void storeResponse(JsonStore jsonStore);

    void storeError();

    void authenticationFailure(int errorCode);

}
