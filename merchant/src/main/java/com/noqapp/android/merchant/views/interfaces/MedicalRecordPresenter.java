package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MedicalRecordPresenter extends ResponseErrorPresenter{

    void medicalRecordResponse(JsonResponse jsonResponse);

    void medicalRecordError();

}
