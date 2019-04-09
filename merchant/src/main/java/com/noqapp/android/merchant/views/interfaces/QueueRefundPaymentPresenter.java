package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

public interface QueueRefundPaymentPresenter extends ResponseErrorPresenter {

    void queueRefundPaymentResponse(JsonQueuedPerson jsonQueuedPerson);

}


