package com.noqapp.android.client.model;

import com.noqapp.android.client.ITest;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;

import static com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum.MOBILE_JSON;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeviceApiCallITest extends ITest {

    @Mock private AppBlacklistPresenter appBlacklistPresenter;
    @Mock private DeviceRegisterPresenter deviceRegisterPresenter;
    private DeviceApiCall deviceApiCall;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.deviceApiCall = new DeviceApiCall();
        this.deviceApiCall.setAppBlacklistPresenter(appBlacklistPresenter);
        this.deviceApiCall.setDeviceRegisterPresenter(deviceRegisterPresenter);
    }

    @Test
    void register_Fail() {
        DeviceToken deviceToken = new DeviceToken(did, Constants.appVersion(), null);
        this.deviceApiCall.register(deviceToken);
        await().atMost(1, MINUTES).pollInterval(10, SECONDS).until(awaitUntilResponseFromServer());
        assertEquals(MOBILE_JSON, MobileSystemErrorCodeEnum.valueOf(deviceApiCall.getErrorEncounteredJson().getSystemError()));
    }

    @Test
    void isSupportedAppVersion() {
        this.deviceApiCall.isSupportedAppVersion();
        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilResponseFromServer());
        assertEquals("1.3.150", deviceApiCall.getJsonLatestAppVersion().getLatestAppVersion());
    }

    private Callable<Boolean> awaitUntilResponseFromServer() {
        return () -> this.deviceApiCall.isResponseReceived();
    }
}