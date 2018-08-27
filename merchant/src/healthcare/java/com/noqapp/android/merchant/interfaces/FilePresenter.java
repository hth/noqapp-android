package com.noqapp.android.merchant.interfaces;

import java.io.File;

public interface FilePresenter {

    void fileResponse(File temp);

    void fileError();

    void authenticationFailure(int errorCode);
}
