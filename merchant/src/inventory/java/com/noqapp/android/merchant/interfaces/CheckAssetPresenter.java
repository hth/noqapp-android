package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAssetList;


public interface CheckAssetPresenter extends ResponseErrorPresenter {

    void jsonCheckAssetListResponse(JsonCheckAssetList jsonCheckAssetList);

}
