package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonReviewBucket;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface AllReviewPresenter extends ResponseErrorPresenter {

    void allReviewResponse(JsonReviewBucket jsonReviewBucket);
}
