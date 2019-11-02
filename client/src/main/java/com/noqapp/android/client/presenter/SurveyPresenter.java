package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonQuestionnaire;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface SurveyPresenter extends ResponseErrorPresenter {

    void surveyResponse(JsonQuestionnaire jsonQuestionnaire);
}
