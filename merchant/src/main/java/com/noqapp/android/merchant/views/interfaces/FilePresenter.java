package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;

import java.io.File;

public interface FilePresenter extends ResponseErrorPresenter {

    void fileResponse(File temp);

    void fileError();
}
