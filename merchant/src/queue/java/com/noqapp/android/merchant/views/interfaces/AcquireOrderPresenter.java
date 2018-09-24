package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonToken;

public interface AcquireOrderPresenter extends ResponseErrorPresenter{

    void acquireOrderResponse(JsonToken token);
}
