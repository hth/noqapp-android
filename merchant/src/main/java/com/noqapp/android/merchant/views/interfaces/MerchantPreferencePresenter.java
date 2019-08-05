package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonUserPreference;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MerchantPreferencePresenter extends ResponseErrorPresenter {

    void merchantPreferencePresenterResponse(JsonUserPreference jsonUserPreference);
}
