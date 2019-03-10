package com.noqapp.android.client.model;

import com.noqapp.android.client.ITest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaceOrderTest extends ITest {


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

    }


//    private Callable<Boolean> awaitUntilResponseFromServer() {
//       // return () -> this.queueApiAuthenticCall.isResponseReceived();
//    }
}
