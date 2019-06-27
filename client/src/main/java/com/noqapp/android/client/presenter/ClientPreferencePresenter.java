package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonUserPreference;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface ClientPreferencePresenter extends ResponseErrorPresenter {

    void clientPreferencePresenterResponse(JsonUserPreference jsonUserPreference);
}
