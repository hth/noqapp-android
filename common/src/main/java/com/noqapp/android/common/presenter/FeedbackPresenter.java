package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.JsonResponse;

public interface FeedbackPresenter extends ResponseErrorPresenter{

    void feedbackResponse(JsonResponse jsonResponse);

}