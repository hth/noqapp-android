package com.noqapp.client.presenter;

import com.noqapp.client.presenter.beans.JsonResponse;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */

public interface ReviewPresenter {

    void reviewResponse(JsonResponse jsonResponse);

    void reviewError();
}
