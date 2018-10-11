package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface AllReviewPresenter extends ResponseErrorPresenter {

    void allReviewResponse(JsonReviewList jsonReviewList);

}
