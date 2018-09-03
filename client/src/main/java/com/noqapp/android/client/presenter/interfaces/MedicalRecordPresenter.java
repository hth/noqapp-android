package com.noqapp.android.client.presenter.interfaces;

import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;


public interface MedicalRecordPresenter {

    void medicalRecordResponse(JsonMedicalRecordList jsonMedicalRecordList);

    void medicalRecordError();

    void authenticationFailure(int errorCode);

}
