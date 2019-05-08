package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.medical.JsonMedicalProfile;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface MedicalRecordProfilePresenter extends ResponseErrorPresenter {

    void medicalRecordProfileResponse(JsonMedicalProfile jsonMedicalProfile);
}
