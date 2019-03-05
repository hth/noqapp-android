package com.noqapp.android.merchant.presenter;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonVigyaapanTVList;

public interface VigyaapanPresenter extends ResponseErrorPresenter {
    void vigyaapanResponse(JsonVigyaapanTVList jsonVigyaapanTVList);
}

