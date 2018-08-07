package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;

public interface AcquireOrderPresenter {

    void acquireOrderResponse(JsonToken token);

    void acquireOrderError(ErrorEncounteredJson errorEncounteredJson);

    void authenticationFailure(int errorCode);
}
