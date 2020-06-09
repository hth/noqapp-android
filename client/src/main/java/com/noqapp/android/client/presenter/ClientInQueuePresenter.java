package com.noqapp.android.client.presenter;


import com.noqapp.android.client.presenter.beans.JsonInQueuePerson;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface ClientInQueuePresenter extends ResponseErrorPresenter {

    void clientInQueueResponse(JsonInQueuePerson jsonInQueuePerson);

    void clientInQueueErrorPresenter(ErrorEncounteredJson eej);

}