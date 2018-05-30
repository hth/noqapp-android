package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.medical.JsonMedicalRecordList;


public interface MedicalRecordPresenter {

    void medicalRecordResponse(JsonMedicalRecordList jsonMedicalRecordList);

    void medicalRecordError();

    void authenticationFailure(int errorCode);

}
