package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;


public interface ManageQueuePresenter {

    void manageQueueResponse(JsonToken token);

    void manageQueueError(ErrorEncounteredJson errorEncounteredJson);

    void authenticationFailure(int errorCode);

}
