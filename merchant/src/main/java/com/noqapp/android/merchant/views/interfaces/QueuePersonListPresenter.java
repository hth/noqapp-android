package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;


public interface QueuePersonListPresenter extends ResponseErrorPresenter{
    void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList);

    void queuePersonListError();

    void authenticationFailure(int errorCode);
}
