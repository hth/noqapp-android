package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.common.beans.JsonResponse;

public interface MedicalRecordPresenter {

    void medicalRecordResponse(JsonResponse jsonResponse);

    void medicalRecordError();

    void authenticationFailure(int errorCode);

}
