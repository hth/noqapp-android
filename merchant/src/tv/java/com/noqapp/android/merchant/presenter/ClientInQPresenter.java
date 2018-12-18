package com.noqapp.android.merchant.presenter;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;

public interface ClientInQPresenter extends ResponseErrorPresenter {

    void ClientInResponse(JsonQueueTVList jsonQueueTVList);
}
