package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.medical.JsonImmunizationList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface ImmunizationHistoryPresenter extends ResponseErrorPresenter {

    void immunizationHistoryResponse(JsonImmunizationList jsonImmunizationList);

}

