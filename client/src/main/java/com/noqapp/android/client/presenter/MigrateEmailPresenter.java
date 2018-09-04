package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonResponse;

public interface MigrateEmailPresenter {

    void migrateEmailResponse(JsonResponse jsonResponse);

    void migrateEmailError();

    void migrateEmailError(String error);

    void authenticationFailure(int errorCode);
}
