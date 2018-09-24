package com.noqapp.android.merchant.interfaces;


import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface PatientProfilePresenter extends ResponseErrorPresenter {

    void patientProfileResponse(JsonProfile jsonProfile);

    void patientProfileError();

}
