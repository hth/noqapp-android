package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface ReviewPresenter extends ResponseErrorPresenter {

    void reviewResponse(JsonReview jsonReview);
}
