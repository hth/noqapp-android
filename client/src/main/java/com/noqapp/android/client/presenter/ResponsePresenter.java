package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonResponse;

/**
 * User: hitender
 * Date: 4/1/17 4:25 PM
 */

public interface ResponsePresenter {
    void responsePresenterResponse(JsonResponse response);

    void responsePresenterError();
}
