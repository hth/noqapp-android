package com.noqapp.android.common.presenter;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.common.beans.JsonAdvertisementList;

public interface AdvertisementPresenter extends ResponseErrorPresenter {
    void advertisementResponse(JsonAdvertisementList jsonAdvertisementList);
}

