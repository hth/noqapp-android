package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface FindCustomerPresenter extends ResponseErrorPresenter{

    void findCustomerResponse(JsonProfile jsonProfile);
}
