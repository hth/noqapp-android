package com.noqapp.android.merchant.presenter;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;

public interface ClientInQueuePresenter extends ResponseErrorPresenter {

    void clientInResponse(JsonQueueTVList jsonQueueTVList);
}
