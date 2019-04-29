package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.common.presenter.ResponseErrorPresenter;
import com.noqapp.android.merchant.views.pojos.Receipt;

public interface ReceiptInfoPresenter extends ResponseErrorPresenter {
    void receiptInfoResponse(Receipt receipt);
}
