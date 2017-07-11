package com.noqapp.android.client.utils;

import android.util.Log;

import com.noqapp.android.client.model.types.QueueStatusEnum;
import com.noqapp.android.client.presenter.beans.JsonQueue;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 7/11/17 5:57 AM
 */
public class JoinQueueUtil {
    private static final String TAG = JoinQueueUtil.class.getSimpleName();

    public static boolean canJoinQueue(JsonQueue jsonQueue) {
        boolean allowedToJoinQueue = true;
        if (isPreventJoining(jsonQueue)) {
            Log.d(TAG, "Prevent Joining found");
            return false;
        }

        if (isDayClosed(jsonQueue)) {
            Log.d(TAG, "Closed for Day found");
            return false;
        }

        if (isTokenNotAvailable(jsonQueue)) {
            Log.d(TAG, "Token not available found as now is before "
                    + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom())
                    + " and after "
                    + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenNotAvailableFrom()));
            return false;
        }

        if (isQueueClosedPermanently(jsonQueue)) {
            Log.d(TAG, "Queue closed permanently found");
            return false;
        }

        return allowedToJoinQueue;
    }

    private static boolean isPreventJoining(JsonQueue jsonQueue) {
        return jsonQueue.isPreventJoining();
    }

    private static boolean isDayClosed(JsonQueue jsonQueue) {
        return jsonQueue.isDayClosed();
    }

    /**
     * Check if user is joining the queue between the times allowed.
     *
     * @param jsonQueue
     * @return
     */
    private static boolean isTokenNotAvailable(JsonQueue jsonQueue) {
        DateTime tokenAvailableFrom = Formatter.parseDateTime(Formatter.formatMilitaryTime(jsonQueue.getTokenAvailableFrom()));
        DateTime tokenNotAvailableFrom = Formatter.parseDateTime(Formatter.formatMilitaryTime(jsonQueue.getTokenNotAvailableFrom()));

        return tokenAvailableFrom.isBeforeNow() && tokenNotAvailableFrom.isAfterNow();
    }

    /**
     * When queue status is set to Closed.
     *
     * @param jsonQueue
     * @return
     */
    private static boolean isQueueClosedPermanently(JsonQueue jsonQueue) {
        return jsonQueue.getQueueStatus() == QueueStatusEnum.C;
    }

    private static void doGPS() {

    }

    /**
     * This should prevent unregistered client from joining. This condition should enforce client has
     * to be logged in.
     *
     * @param jsonQueue
     * @return
     */
    private static boolean isAllowLoggedInUser(JsonQueue jsonQueue) {
        return jsonQueue.isAllowLoggedInUser();
    }
}
