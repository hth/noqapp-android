package com.noqapp.android.client;

import com.noqapp.android.client.model.DeviceApiCall;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.util.Log;

import java.io.IOException;

public class ITest {


    protected DeviceApiCall deviceApiCall;

    @Mock protected Log log;

    @BeforeAll
    public void globalISetup() throws IOException {
        MockitoAnnotations.initMocks(this);

        this.deviceApiCall = new DeviceApiCall();
    }


}
