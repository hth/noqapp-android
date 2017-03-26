package com.noqapp.user.Presenter;

import com.noqapp.user.Presenter.Beans.ScanQRCode;

/**
 * Created by omkar on 3/26/17.
 */

public interface QRCodePresenter {

    public void didQRCodeResponse(ScanQRCode qrCode);
    public void didQRCodeError();

}
