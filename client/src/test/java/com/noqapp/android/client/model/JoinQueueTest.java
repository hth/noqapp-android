package com.noqapp.android.client.model;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.noqapp.android.client.ITest;
import com.noqapp.android.client.model.api.TokenQueueApiImpl;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.TokenPresenter;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.common.beans.body.JoinQueue;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JoinQueueTest extends ITest {

    private TokenQueueApiImpl tokenQueueApiImpl;
    private JsonQueue jsonQueueTemp;
    private JsonToken jsonToken;
    @Mock private QueuePresenter queuePresenter;
    @Mock private TokenPresenter tokenPresenter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenQueueApiImpl = new TokenQueueApiImpl();
        tokenQueueApiImpl.setQueuePresenter(queuePresenter);
        tokenQueueApiImpl.setTokenPresenter(tokenPresenter);
    }

    @Test
    void joinQueue() {
        tokenQueueApiImpl.setQueuePresenter(queuePresenter);
        tokenQueueApiImpl.getQueueState(did, emailid, auth, codeQR);
        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilResponseFromServer());
        Assertions.assertTrue(null != tokenQueueApiImpl.jsonQueue, "Store not found");
        jsonQueueTemp = tokenQueueApiImpl.jsonQueue;
    }

    @Test
    void afterJoinQueue() {
        if (null != jsonQueueTemp) {
            tokenQueueApiImpl.setResponseReceived(false);
            JoinQueue joinQueue = new JoinQueue().setCodeQR(codeQR).setQueueUserId(queueUserId);
            tokenQueueApiImpl.joinQueue(did, emailid, auth, joinQueue);
            await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilResponseFromServer());
            jsonToken = tokenQueueApiImpl.jsonToken;
            Assertions.assertTrue(jsonQueueTemp.getLastNumber() <= jsonToken.getToken(), "Token can not be negative or less than token available from");
        }

    }

    private Callable<Boolean> awaitUntilResponseFromServer() {
        return () -> this.tokenQueueApiImpl.isResponseReceived();
    }
}
