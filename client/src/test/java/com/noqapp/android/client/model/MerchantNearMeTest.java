package com.noqapp.android.client.model;

import com.noqapp.android.client.ITest;
import com.noqapp.android.client.model.open.SearchImpl;
import com.noqapp.android.client.model.open.StoreDetailImpl;
import com.noqapp.android.client.model.open.TokenQueueImpl;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.presenter.beans.body.SearchQuery;

import org.junit.jupiter.api.Assertions;
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
    private SearchImpl searchImpl;
    private TokenQueueImpl tokenQueueImpl;
    private StoreDetailImpl storeDetailImpl;
    @Mock
    private SearchBusinessStorePresenter searchBusinessStorePresenter;
    @Mock
    private StorePresenter storePresenter;
    @Mock
    private QueuePresenter queuePresenter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchImpl = new SearchImpl(searchBusinessStorePresenter);
        storeDetailImpl = new StoreDetailImpl(storePresenter);
    }

    @Test
    void searchBusiness() {
        SearchQuery searchQuery = new SearchQuery()
            .setCityName("Mumbai")
            .setLatitude("19.076")
            .setLongitude("72.8777")
            .setFilters("xyz")
            .setScrollId("")
            .setFrom(0)
            .setSize(30);
        searchImpl.business(did, searchQuery);
        System.out.println("Merchant list api called");

        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilResponseFromServer());
        Assertions.assertTrue(searchImpl.bizStoreElasticList.getBizStoreElastics().size() != 0, "no store found");
        bizStoreElasticList = searchImpl.bizStoreElasticList;

        if (null != bizStoreElasticList && bizStoreElasticList.getBizStoreElastics().size() > 0) {
            searchImpl.setResponseReceived(false);
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
            Assertions.assertTrue(null != bizStoreElasticList, "Stores not found");
        }
    }

    @DisplayName("Calling level up business")
    void callLevelUp(BizStoreElastic bizStoreElastic) {
        tokenQueueImpl = new TokenQueueImpl();
        tokenQueueImpl.setQueuePresenter(queuePresenter);
        tokenQueueImpl.getAllQueueStateLevelUp(did, bizStoreElastic.getCodeQR());
        System.out.println("Level up api called");
        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilQResponseFromServer());
        Assertions.assertNotNull(tokenQueueImpl.bizStoreElasticList, "Store not found");
        if (null != tokenQueueImpl.bizStoreElasticList) {
            System.out.println("Levelup response: \n" + tokenQueueImpl.bizStoreElasticList.toString());
        }
        tokenQueueImpl.setResponseReceived(false);
    }

    @DisplayName("Calling Store")
    void callStoreDetail(BizStoreElastic bizStoreElastic) {
        storeDetailImpl.getStoreDetail(did, bizStoreElastic.getCodeQR());
        System.out.println("Store Api called: ");
        await().atMost(TIME_OUT, SECONDS).pollInterval(POLL_INTERVAL, SECONDS).until(awaitUntilStoreResponseFromServer());
        Assertions.assertNotNull(storeDetailImpl.jsonStore, "Store not found");

        storeDetailImpl.setResponseReceived(false);
        jsonStore = storeDetailImpl.jsonStore;
        if (null != jsonStore) {
            System.out.println("Store Found response: \n" + storeDetailImpl.jsonStore.toString());
        }
    }

    private Callable<Boolean> awaitUntilResponseFromServer() {
        return () -> this.searchImpl.isResponseReceived();
    }

    private Callable<Boolean> awaitUntilStoreResponseFromServer() {
        return () -> this.storeDetailImpl.isResponseReceived();
    }

    private Callable<Boolean> awaitUntilQResponseFromServer() {
        return () -> this.tokenQueueImpl.isResponseReceived();
    }
}
