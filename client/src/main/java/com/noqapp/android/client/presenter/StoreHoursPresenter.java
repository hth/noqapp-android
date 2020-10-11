package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonHourList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

public interface StoreHoursPresenter extends ResponseErrorPresenter {

    void storeHoursResponse(JsonHourList jsonHourList);
}
