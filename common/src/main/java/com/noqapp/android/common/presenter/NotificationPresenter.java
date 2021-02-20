package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;

public interface NotificationPresenter extends ResponseErrorPresenter {

    void notificationResponse(JsonResponse jsonResponse);
}
