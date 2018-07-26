package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;

public interface MedicalRecordListPresenter {

    void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList);

    void medicalRecordListError();

    void authenticationFailure(int errorCode);

}
