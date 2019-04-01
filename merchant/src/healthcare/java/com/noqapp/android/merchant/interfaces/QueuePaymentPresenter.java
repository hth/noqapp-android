package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

public interface QueuePaymentPresenter extends ResponseErrorPresenter {

    void queuePaymentResponse(JsonQueuedPerson jsonQueuedPerson);

}


