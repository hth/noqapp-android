package com.noqapp.android.client.model;

import com.noqapp.android.client.ITest;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MerchantNearMeTest extends ITest {

    private BizStoreElasticList bizStoreElasticList;
    private JsonStore jsonStore;
    private SearchBusinessStoreApiCalls searchBusinessStoreApiCalls;
    private QueueApiUnAuthenticCall queueApiUnAuthenticCall;
    private StoreDetailApiCalls storeDetailApiCalls;
    @Mock
    private SearchBusinessStorePresenter searchBusinessStorePresenter;
    @Mock
    private StorePresenter storePresenter;
    @Mock
    private QueuePresenter queuePresenter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        searchBusinessStoreApiCalls = new SearchBusinessStoreApiCalls(searchBusinessStorePresenter);
        storeDetailApiCalls = new StoreDetailApiCalls(storePresenter);
    }

    @Test
    void searchBusiness() {
        SearchStoreQuery searchStoreQuery = new SearchStoreQuery();
        searchStoreQuery.setCityName("Mumbai");
        searchStoreQuery.setLatitude("19.076");
        searchStoreQuery.setLongitude("72.8777");
        searchStoreQuery.setFilters("xyz");
        searchStoreQuery.setScrollId("");
        searchBusinessStoreApiCalls.otherMerchant(did, searchStoreQuery);
        System.out.println("Merchant list api called");

        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilResponseFromServer());
        Assert.assertTrue("no store found", searchBusinessStoreApiCalls.bizStoreElasticList.getBizStoreElastics().size() != 0);
        bizStoreElasticList = searchBusinessStoreApiCalls.bizStoreElasticList;

        if (null != bizStoreElasticList && bizStoreElasticList.getBizStoreElastics().size() > 0) {
            searchBusinessStoreApiCalls.setResponseReceived(false);
            System.out.println("No of stores found: " + bizStoreElasticList.getBizStoreElastics().size());
            BizStoreElastic bizStoreElastic = bizStoreElasticList.getBizStoreElastics().get(0);
            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                case CD:
                case CDQ:
                case BK:
                case HS:
                    callLevelUp(bizStoreElastic);
                    break;
                default:
                    callStoreDetail(bizStoreElastic);
            }
        } else {
            Assert.assertTrue("Stores not found", null != bizStoreElasticList);
        }
    }


    @DisplayName("Calling level up business")
    void callLevelUp(BizStoreElastic bizStoreElastic) {
        queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
        queueApiUnAuthenticCall.setQueuePresenter(queuePresenter);
        queueApiUnAuthenticCall.getAllQueueStateLevelUp(did, bizStoreElastic.getCodeQR());
        System.out.println("Level up api called");
        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilQResponseFromServer());
        Assert.assertTrue("Store not found", null != queueApiUnAuthenticCall.bizStoreElasticList);
        if (null != queueApiUnAuthenticCall.bizStoreElasticList) {
            System.out.println("Levelup response: \n" + queueApiUnAuthenticCall.bizStoreElasticList.toString());
        }
        queueApiUnAuthenticCall.setResponseReceived(false);
    }

    @DisplayName("Calling Store")
    void callStoreDetail(BizStoreElastic bizStoreElastic) {
        storeDetailApiCalls.getStoreDetail(did, bizStoreElastic.getCodeQR());
        System.out.println("Store Api called: ");
        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilStoreResponseFromServer());
        Assert.assertTrue("Store not found", null != storeDetailApiCalls.jsonStore);

        storeDetailApiCalls.setResponseReceived(false);
        jsonStore = storeDetailApiCalls.jsonStore;
        if (null != jsonStore) {
            System.out.println("Store Found response: \n" + storeDetailApiCalls.jsonStore.toString());
        }
    }

    private Callable<Boolean> awaitUntilResponseFromServer() {
        return () -> this.searchBusinessStoreApiCalls.isResponseReceived();
    }

    private Callable<Boolean> awaitUntilStoreResponseFromServer() {
        return () -> this.storeDetailApiCalls.isResponseReceived();
    }

    private Callable<Boolean> awaitUntilQResponseFromServer() {
        return () -> this.queueApiUnAuthenticCall.isResponseReceived();
    }
}
