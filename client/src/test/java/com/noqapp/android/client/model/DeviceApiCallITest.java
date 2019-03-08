package com.noqapp.android.client.model;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

import com.noqapp.android.client.ITest;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import android.util.Log;
import retrofit2.Call;

import java.util.UUID;
import java.util.concurrent.Callable;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeviceApiCallITest extends ITest {

    private AppBlacklistPresenter appBlacklistPresenter;
    private DeviceRegisterPresenter deviceRegisterPresenter;

    @BeforeEach
    void setUp() {
        this.appBlacklistPresenter = new AppBlacklistPresenter() {
            @Override
            public void appBlacklistError(ErrorEncounteredJson eej) {

            }

            @Override
            public void appBlacklistResponse(JsonLatestAppVersion jsonLatestAppVersion) {

            }

            @Override
            public void responseErrorPresenter(ErrorEncounteredJson eej) {

            }

            @Override
            public void responseErrorPresenter(int errorCode) {

            }

            @Override
            public void authenticationFailure() {

            }
        };

        this.deviceRegisterPresenter = new DeviceRegisterPresenter() {
            @Override
            public void deviceRegisterError() {

            }

            @Override
            public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {

            }

            @Override
            public void responseErrorPresenter(ErrorEncounteredJson eej) {

            }

            @Override
            public void responseErrorPresenter(int errorCode) {

            }

            @Override
            public void authenticationFailure() {

            }
        };

        this.deviceApiCall.setAppBlacklistPresenter(appBlacklistPresenter);
        this.deviceApiCall.setDeviceRegisterPresenter(deviceRegisterPresenter);
    }

    @Test
    void setDeviceRegisterPresenter() {
    }

    @Test
    void setAppBlacklistPresenter() {
    }

    @Test
    void register() {
    }

    @Test
    void isSupportedAppVersion() {
        String did = UUID.randomUUID().toString();
        this.deviceApiCall.isSupportedAppVersion(did);
        await().atMost(1, MINUTES).pollInterval(5, SECONDS).until(awaitUntilPurchaseOrderIsPlaced());
        assertEquals("1.2.230", deviceApiCall.getJsonLatestAppVersion().getLatestAppVersion());
    }

    private Callable<Boolean> awaitUntilPurchaseOrderIsPlaced() {
        return () -> this.deviceApiCall.isResponseReceived();
    }
}