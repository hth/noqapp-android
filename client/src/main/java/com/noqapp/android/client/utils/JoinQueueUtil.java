package com.noqapp.android.client.utils;

import android.content.Context;
import android.util.Log;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.QueueStatusEnum;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 7/11/17 5:57 AM
 */
public class JoinQueueUtil {
    private static final String TAG = JoinQueueUtil.class.getSimpleName();

    public static JoinQueueState canJoinQueue(JsonQueue jsonQueue, Context context) {
        JoinQueueState joinQueueState = new JoinQueueState();
        joinQueueState.setJoinNotPossible(false);

        if (isPreventJoining(jsonQueue)) {
            Log.d(TAG, "Prevent Joining found");
            String msg = String.format(
                    context.getString(R.string.error_prevent_joining),
                    jsonQueue.getBusinessName(),
                    jsonQueue.getDisplayName());

            joinQueueState.setJoinNotPossible(true)
                    .setJoinErrorMsg(msg);
        }

        if (isDayClosed(jsonQueue)) {
            Log.d(TAG, "Closed for Day found");
            String msg = String.format(
                    context.getString(R.string.error_day_closed),
                    jsonQueue.getBusinessName(),
                    jsonQueue.getDisplayName());

            joinQueueState.setJoinNotPossible(true)
                    .setJoinErrorMsg(msg);
        }

        if (isTokenNotAvailable(jsonQueue)) {
            Log.d(TAG, "Token not available found as now is before "
                    + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom())
                    + " and after "
                    + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenNotAvailableFrom()));
            joinQueueState.setJoinNotPossible(true);

            DateTime tokenAvailableFrom = Formatter.parseDateTime(Formatter.formatMilitaryTime(jsonQueue.getTokenAvailableFrom()));
            DateTime tokenNotAvailableFrom = Formatter.parseDateTime(Formatter.formatMilitaryTime(jsonQueue.getTokenNotAvailableFrom()));

            if (tokenAvailableFrom.isBeforeNow()) {
                String startTime = Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom());
                String msg = String.format(
                        context.getString(R.string.error_token_available_from),
                        jsonQueue.getBusinessName(),
                        jsonQueue.getDisplayName(),
                        startTime);

                joinQueueState.setJoinErrorMsg(msg);
            } else if (tokenNotAvailableFrom.isAfterNow()) {
                String msg = String.format(context.getString(R.string.error_token_not_available_from), jsonQueue.getBusinessName(), jsonQueue.getDisplayName());
                joinQueueState.setJoinErrorMsg(msg);
            }
        }

        if (isQueueClosedPermanently(jsonQueue)) {
            Log.d(TAG, "Queue closed permanently found");
            String msg = String.format(
                    context.getString(R.string.error_business_closed_permanent),
                    jsonQueue.getBusinessName(),
                    jsonQueue.getDisplayName());

            joinQueueState.setJoinNotPossible(true)
                    .setJoinErrorMsg(msg);
        }
        return joinQueueState;
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
