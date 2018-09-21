package com.noqapp.android.merchant.views.interfaces;


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonToken;


public interface ManageQueuePresenter extends ResponseErrorPresenter{

    void manageQueueResponse(JsonToken token);

    void manageQueueError();

    void authenticationFailure(int errorCode);

}
