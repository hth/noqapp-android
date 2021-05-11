package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */
public interface SearchBusinessStorePresenter extends ResponseErrorPresenter {

    void nearMeMerchant(BizStoreElasticList bizStoreElasticList);

    void nearMeMerchantError();

    void nearMeHospitalResponse(BizStoreElasticList bizStoreElasticList);

    void nearMeHospitalError();

    void nearMeCanteenResponse(BizStoreElasticList bizStoreElasticList);

    void nearMeCanteenError();

    void nearMeRestaurantsResponse(BizStoreElasticList bizStoreElasticList);

    void nearMeRestaurantsError();

    void nearMeTempleResponse(BizStoreElasticList bizStoreElasticList);

    void businessList(BizStoreElasticList bizStoreElasticList);

    void nearMeTempleError();
}
