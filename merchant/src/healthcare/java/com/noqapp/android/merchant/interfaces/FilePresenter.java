package com.noqapp.android.merchant.interfaces;

import com.noqapp.android.common.presenter.AuthenticationFailure;

import java.io.File;

public interface FilePresenter extends AuthenticationFailure {

    void fileResponse(File temp);

    void fileError();
}
