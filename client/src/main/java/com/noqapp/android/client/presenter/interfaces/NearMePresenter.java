package com.noqapp.android.client.presenter.interfaces;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */

public interface NearMePresenter {

    void nearMeResponse(BizStoreElasticList bizStoreElasticList);

    void nearMeError();
}
