package com.noqapp.android.merchant.presenter;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonAdvertisementList;

public interface AdvertisementPresenter extends ResponseErrorPresenter {
    void advertisementResponse(JsonAdvertisementList jsonAdvertisementList);
}

