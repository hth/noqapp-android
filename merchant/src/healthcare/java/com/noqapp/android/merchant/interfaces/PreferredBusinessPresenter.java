package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;

public interface PreferredBusinessPresenter extends ResponseErrorPresenter{

    void preferredBusinessResponse(JsonPreferredBusinessList jsonPreferredBusinessList);

    void preferredBusinessError();

}
