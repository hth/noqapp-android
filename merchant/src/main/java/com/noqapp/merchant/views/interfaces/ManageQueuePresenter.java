package com.noqapp.merchant.views.interfaces;


import com.noqapp.merchant.presenter.beans.JsonToken;



public interface ManageQueuePresenter {
    void manageQueueResponse(JsonToken token);

    void manageQueueError();
}
