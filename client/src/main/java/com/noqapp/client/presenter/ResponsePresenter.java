package com.noqapp.client.presenter;

import com.noqapp.client.presenter.beans.JsonResponse;

/**
 * User: hitender
 * Date: 4/1/17 4:25 PM
 */

public interface ResponsePresenter {
    void responsePresenterResponse(JsonResponse response);

    void responsePresenterError();
}
