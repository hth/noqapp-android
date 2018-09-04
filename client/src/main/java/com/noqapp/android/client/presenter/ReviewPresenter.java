package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonResponse;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */

public interface ReviewPresenter {

    void reviewResponse(JsonResponse jsonResponse);

    void reviewError();

    void authenticationFailure(int errorCode);
}
