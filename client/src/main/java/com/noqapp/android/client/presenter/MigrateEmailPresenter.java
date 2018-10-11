package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MigrateEmailPresenter extends ResponseErrorPresenter {

    void migrateEmailResponse(JsonResponse jsonResponse);

}
