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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.util.Log;
import retrofit2.Call;

import java.util.UUID;
import java.util.concurrent.Callable;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeviceApiCallITest extends ITest {

    @Mock private AppBlacklistPresenter appBlacklistPresenter;
    @Mock private DeviceRegisterPresenter deviceRegisterPresenter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

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
        await().atMost(30, SECONDS).pollInterval(5, SECONDS).until(awaitUntilResponseFromServer());
        assertEquals("1.2.230", deviceApiCall.getJsonLatestAppVersion().getLatestAppVersion());
    }

    private Callable<Boolean> awaitUntilResponseFromServer() {
        return () -> this.deviceApiCall.isResponseReceived();
    }
}