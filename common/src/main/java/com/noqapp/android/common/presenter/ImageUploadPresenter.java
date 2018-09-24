package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;

public interface ImageUploadPresenter extends ResponseErrorPresenter{

    void imageUploadResponse(JsonResponse jsonResponse);

    void imageUploadError();

}
