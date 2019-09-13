package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by hitender on 1/3/18.
 */
public class CategoryInfoFragmentTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testJsonQueueSort1() {
        LocalTime now = LocalTime.now();
        final int systemHourMinutes = Integer.parseInt(String.valueOf(now.getHour()) + String.format(Locale.US, "%02d", now.getMinute()));
        JsonQueue jsonQueue1 = new JsonQueue()
                .setDisplayName("Before Token Available")
                .setStartHour(systemHourMinutes + 100)
                .setEndHour(systemHourMinutes + 200)
                .setTokenAvailableFrom(systemHourMinutes + 30)
                .setTokenNotAvailableFrom(systemHourMinutes + 200)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueue2 = new JsonQueue()
                .setDisplayName("Open Now")
                .setStartHour(systemHourMinutes)
                .setEndHour(systemHourMinutes + 100)
                .setTokenAvailableFrom(systemHourMinutes)
                .setTokenNotAvailableFrom(systemHourMinutes + 100)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueueClosed = new JsonQueue()
                .setDisplayName("Closed Today")
                .setStartHour(systemHourMinutes + 100)
                .setEndHour(systemHourMinutes + 200)
                .setTokenAvailableFrom(systemHourMinutes + 30)
                .setTokenNotAvailableFrom(systemHourMinutes + 200)
                .setDayClosed(true)
                .setCodeQR(UUID.randomUUID().toString());

        List<JsonQueue> jsonQueues = new ArrayList<>();
        jsonQueues.add(jsonQueueClosed);
        jsonQueues.add(jsonQueue1);
        jsonQueues.add(jsonQueue2);

        AppUtils.sortJsonQueues(systemHourMinutes, jsonQueues);
        Assert.assertEquals(jsonQueue2, jsonQueues.get(0));
        Assert.assertEquals(jsonQueue1, jsonQueues.get(1));
        Assert.assertEquals(jsonQueueClosed, jsonQueues.get(2));

        /*
        Open Now
        Before Token Available
        Closed Today
        */
    }

    @Test
    public void testJsonQueueSort2() {
        LocalTime now = LocalTime.now();
        final int systemHourMinutes = Integer.parseInt(String.valueOf(now.getHour()) + String.format(Locale.US, "%02d", now.getMinute()));
        JsonQueue jsonQueue1 = new JsonQueue()
                .setDisplayName("Before Token Available")
                .setStartHour(systemHourMinutes + 100)
                .setEndHour(systemHourMinutes + 200)
                .setTokenAvailableFrom(systemHourMinutes + 30)
                .setTokenNotAvailableFrom(systemHourMinutes + 200)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueue2 = new JsonQueue()
                .setDisplayName("Open Now")
                .setStartHour(systemHourMinutes)
                .setEndHour(systemHourMinutes + 100)
                .setTokenAvailableFrom(systemHourMinutes)
                .setTokenNotAvailableFrom(systemHourMinutes + 100)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueueClosed = new JsonQueue()
                .setDisplayName("Closed Today")
                .setStartHour(systemHourMinutes + 100)
                .setEndHour(systemHourMinutes + 200)
                .setTokenAvailableFrom(systemHourMinutes + 30)
                .setTokenNotAvailableFrom(systemHourMinutes + 200)
                .setDayClosed(true)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueue3 = new JsonQueue()
                .setDisplayName("Closed Now")
                .setStartHour(systemHourMinutes - 100)
                .setEndHour(systemHourMinutes - 1)
                .setTokenAvailableFrom(systemHourMinutes - 130)
                .setTokenNotAvailableFrom(systemHourMinutes)
                .setCodeQR(UUID.randomUUID().toString());

        List<JsonQueue> jsonQueues = new ArrayList<>();
        jsonQueues.add(jsonQueueClosed);
        jsonQueues.add(jsonQueue1);
        jsonQueues.add(jsonQueue2);
        jsonQueues.add(jsonQueue3);

        AppUtils.sortJsonQueues(systemHourMinutes, jsonQueues);
        Assert.assertEquals(jsonQueue2, jsonQueues.get(0));
        Assert.assertEquals(jsonQueue1, jsonQueues.get(1));
        Assert.assertEquals(jsonQueue3, jsonQueues.get(2));
        Assert.assertEquals(jsonQueueClosed, jsonQueues.get(3));

        /*
        Open Now
        Before Token Available
        Closed Now
        Closed Today
        */
    }

    @Test
    public void testJsonQueueSort3() {
        LocalTime now = LocalTime.now();
        final int systemHourMinutes = Integer.parseInt(String.valueOf(now.getHour()) + String.format(Locale.US, "%02d", now.getMinute()));
        JsonQueue jsonQueue1 = new JsonQueue()
                .setDisplayName("Before Token Available 1")
                .setStartHour(systemHourMinutes + 100)
                .setEndHour(systemHourMinutes + 200)
                .setTokenAvailableFrom(systemHourMinutes + 30)
                .setTokenNotAvailableFrom(systemHourMinutes + 200)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueue2 = new JsonQueue()
                .setDisplayName("Before Token Available 2")
                .setStartHour(systemHourMinutes + 200)
                .setEndHour(systemHourMinutes + 300)
                .setTokenAvailableFrom(systemHourMinutes + 100)
                .setTokenNotAvailableFrom(systemHourMinutes + 200)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueueClosed = new JsonQueue()
                .setDisplayName("Closed Today")
                .setStartHour(systemHourMinutes + 100)
                .setEndHour(systemHourMinutes + 200)
                .setTokenAvailableFrom(systemHourMinutes + 30)
                .setTokenNotAvailableFrom(systemHourMinutes + 200)
                .setDayClosed(true)
                .setCodeQR(UUID.randomUUID().toString());

        JsonQueue jsonQueue3 = new JsonQueue()
                .setDisplayName("Closed Now")
                .setStartHour(systemHourMinutes - 100)
                .setEndHour(systemHourMinutes - 1)
                .setTokenAvailableFrom(systemHourMinutes - 130)
                .setTokenNotAvailableFrom(systemHourMinutes)
                .setCodeQR(UUID.randomUUID().toString());

        List<JsonQueue> jsonQueues = new ArrayList<>();
        jsonQueues.add(jsonQueueClosed);
        jsonQueues.add(jsonQueue1);
        jsonQueues.add(jsonQueue2);
        jsonQueues.add(jsonQueue3);

        AppUtils.sortJsonQueues(systemHourMinutes, jsonQueues);
        Assert.assertEquals(jsonQueue1, jsonQueues.get(0));
        Assert.assertEquals(jsonQueue2, jsonQueues.get(1));
        Assert.assertEquals(jsonQueue3, jsonQueues.get(2));
        Assert.assertEquals(jsonQueueClosed, jsonQueues.get(3));

        /*
        Before Token Available 1
        Before Token Available 2
        Closed Now
        Closed Today
        */
    }
}
