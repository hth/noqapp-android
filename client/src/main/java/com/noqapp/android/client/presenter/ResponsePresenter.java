package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * User: hitender
 * Date: 4/1/17 4:25 PM
 */

public interface ResponsePresenter extends ResponseErrorPresenter {

    void responsePresenterResponse(JsonResponse response);

    void responsePresenterError();

    void authenticationFailure(int errorCode);
}
