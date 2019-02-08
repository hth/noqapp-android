package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessBucket;

public interface PreferredBusinessPresenter extends ResponseErrorPresenter{

    void preferredBusinessResponse(JsonPreferredBusinessBucket jsonPreferredBusinessBucket);

    void preferredBusinessError();

}
