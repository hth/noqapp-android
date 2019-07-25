package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface CashFreeNotifyQPresenter extends ResponseErrorPresenter {

    void cashFreeNotifyQResponse(JsonToken jsonToken);
}
