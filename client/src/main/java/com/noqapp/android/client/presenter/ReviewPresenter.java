package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */

public interface ReviewPresenter extends ResponseErrorPresenter{

    void reviewResponse(JsonResponse jsonResponse);

}
