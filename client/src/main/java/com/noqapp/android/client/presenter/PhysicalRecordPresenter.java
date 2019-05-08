package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.medical.JsonMedicalPhysicalList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface PhysicalRecordPresenter extends ResponseErrorPresenter {
    void physicalRecordResponse(JsonMedicalPhysicalList jsonMedicalPhysicalList);
}
