package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MedicalRecordListPresenter extends ResponseErrorPresenter{

    void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList);

    void medicalRecordListError();

    void authenticationFailure(int errorCode);

}
