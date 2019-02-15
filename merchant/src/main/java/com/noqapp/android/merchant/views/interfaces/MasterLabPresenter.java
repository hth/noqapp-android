package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MasterLabPresenter extends ResponseErrorPresenter {

    void masterLabUploadResponse(JsonResponse jsonResponse);

}
