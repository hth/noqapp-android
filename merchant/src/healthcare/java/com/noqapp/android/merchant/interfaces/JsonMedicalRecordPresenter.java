package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface JsonMedicalRecordPresenter extends ResponseErrorPresenter {

    void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord);

}
