package com.noqapp.client.Presenter;

import com.noqapp.client.Presenter.Beans.ScanQRCode;

/**
 * Created by omkar on 3/26/17.
 */

public interface QRCodePresenter {

    public void didQRCodeResponse(ScanQRCode qrCode);

    public void didQRCodeError();

}
