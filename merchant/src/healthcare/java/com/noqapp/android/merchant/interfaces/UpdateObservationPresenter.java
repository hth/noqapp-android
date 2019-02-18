package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface UpdateObservationPresenter extends ResponseErrorPresenter {

    void updateObservationResponse(JsonResponse jsonResponse);

}


