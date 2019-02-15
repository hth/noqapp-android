package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.presenter.beans.body.store.LabFile;

public interface LabFilePresenter extends ResponseErrorPresenter {

    void showAttachmentResponse(LabFile labFile);
}
