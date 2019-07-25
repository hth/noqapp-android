package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface StorePresenter extends ResponseErrorPresenter{

    void storeResponse(JsonStore jsonStore);
}
