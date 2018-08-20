package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;

public interface PreferredBusinessPresenter {

    void preferredBusinessResponse(JsonPreferredBusinessList jsonPreferredBusinessList);

    void preferredBusinessError();

    void authenticationFailure(int errorCode);
}
