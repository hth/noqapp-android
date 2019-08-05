package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CheckAsset;


public interface BizNamePresenter extends ResponseErrorPresenter {

    void bizNameResponse(CheckAsset checkAsset);

}
