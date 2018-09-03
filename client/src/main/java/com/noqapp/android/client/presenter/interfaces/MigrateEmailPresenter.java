package com.noqapp.android.client.presenter.interfaces;

import com.noqapp.android.common.beans.JsonResponse;

public interface MigrateEmailPresenter {

    void migrateEmailResponse(JsonResponse jsonResponse);

    void migrateEmailError();

    void authenticationFailure(int errorCode);
}
