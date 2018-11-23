package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MasterLabPresenter extends ResponseErrorPresenter {

    void masterLabUploadResponse(JsonResponse jsonResponse);

}
