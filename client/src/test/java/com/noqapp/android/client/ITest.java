package com.noqapp.android.client;

import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.util.Log;

import java.io.IOException;

public class ITest {

    protected final int TIME_OUT = 30;
    protected final int POLL_INTERVAL = 5;
    protected String codeQR = "5c3ec473b85cb7652cac1276";
    protected String queueUserId = "100000000072";
    protected String auth = "$2a$15$u9y69QSWxi07hbkHUu2gdOx5KD0N/b6yh0tf6DGLb2MLPCb5zgfdm";
    protected String emailid = "raj@e.com";
    protected String did = "1aeddcdc-36ec-43f0-be78-957892ff7954";


    @Mock protected Log log;
    @BeforeAll
    public void globalISetup() throws IOException {
        MockitoAnnotations.initMocks(this);

    }

}
