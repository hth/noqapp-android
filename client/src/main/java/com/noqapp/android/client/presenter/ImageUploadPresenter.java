package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonResponse;

public interface ImageUploadPresenter {

    void imageUploadResponse(JsonResponse jsonResponse);

    void imageUploadError();

    void authenticationFailure(int errorCode);
}
