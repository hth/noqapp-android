package com.noqapp.android.merchant.interfaces;


import com.noqapp.android.common.beans.JsonProfile;

public interface PatientProfilePresenter {

    void patientProfileResponse(JsonProfile jsonProfile);

    void patientProfileError();

    void authenticationFailure(int errorCode);
}
