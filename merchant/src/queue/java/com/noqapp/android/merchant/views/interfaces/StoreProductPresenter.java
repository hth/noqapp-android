package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;

public interface StoreProductPresenter extends ResponseErrorPresenter{

    void storeProductResponse(JsonStore jsonStore);
}
