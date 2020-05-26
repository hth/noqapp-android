package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;

public interface ApproveCustomerPresenter extends ResponseErrorPresenter {

    void approveCustomerResponse(JsonQueuedPerson jsonQueuedPerson);
}
