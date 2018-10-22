package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface ActionOnProductPresenter extends ResponseErrorPresenter {

    void actionOnProductResponse(JsonResponse jsonResponse);

}
