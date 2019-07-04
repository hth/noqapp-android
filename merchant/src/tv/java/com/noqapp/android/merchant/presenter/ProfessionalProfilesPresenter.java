package com.noqapp.android.merchant.presenter;

import com.noqapp.android.common.beans.JsonProfessionalProfileTVList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface ProfessionalProfilesPresenter extends ResponseErrorPresenter {
    void professionalProfilesResponse(JsonProfessionalProfileTVList jsonProfessionalProfileTVList);
}
