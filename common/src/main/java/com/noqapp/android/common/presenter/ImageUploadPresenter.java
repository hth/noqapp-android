package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;

public interface ImageUploadPresenter {

    void imageUploadResponse(JsonResponse jsonResponse);

    void imageUploadError();

    void authenticationFailure(int errorCode);
}
