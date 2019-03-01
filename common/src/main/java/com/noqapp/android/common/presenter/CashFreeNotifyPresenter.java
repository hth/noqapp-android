package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;

public interface CashFreeNotifyPresenter extends ResponseErrorPresenter {

    void cashFreeNotifyResponse(JsonResponse jsonResponse);

}