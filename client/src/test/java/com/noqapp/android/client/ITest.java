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
    protected String queueUserId = "100000000141";
    protected String auth = "$2a$15$WT7vlRnswalpQXHIKhH2fulPHYnRc/jrL9W9zIPwKmkiK1GfujgTC";
    protected String emailid = "bsd@email.com";
    protected String did = "92E12771-751E-460E-98D8-A64224447442";

    @Mock protected Log log;
    @BeforeAll
    public void globalISetup() throws IOException {
        MockitoAnnotations.openMocks(this);
    }
}
