package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MedicalRecordPresenter extends ResponseErrorPresenter {

    void medicalRecordResponse(JsonMedicalRecordList jsonMedicalRecordList);
}
