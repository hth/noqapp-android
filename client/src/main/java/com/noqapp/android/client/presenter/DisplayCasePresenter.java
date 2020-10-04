package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonStoreProductList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface DisplayCasePresenter extends ResponseErrorPresenter {

    void displayCaseResponse(JsonStoreProductList jsonStoreProductList);

    void displayCaseErrorPresenter(ErrorEncounteredJson eej);

}