package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonQueueHistoricalList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface QueueHistoryPresenter extends ResponseErrorPresenter {

    void queueHistoryResponse(JsonQueueHistoricalList jsonQueueHistoricalList);
}