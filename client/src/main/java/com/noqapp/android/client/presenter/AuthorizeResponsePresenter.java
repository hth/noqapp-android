package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface AuthorizeResponsePresenter extends ResponseErrorPresenter {

    void authorizePresenterResponse(JsonResponse response);

    void authorizePresenterError();
}
