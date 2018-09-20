package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * Created by chandra on 11/4/17.
 */

public interface AppBlacklistPresenter extends ResponseErrorPresenter{

    void appBlacklistError();

    void appBlacklistResponse();
}
