package com.noqapp.android.client.utils;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.wrapper.JoinQueueState;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.utils.Formatter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import android.content.Context;
import android.util.Log;

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

        if (!joinQueueState.isJoinNotPossible() && isDayClosed(jsonQueue)) {
            Log.d(TAG, "Closed for Day found");
            String msg = String.format(
                    context.getString(R.string.error_day_closed),
                    jsonQueue.getBusinessName(),
                    jsonQueue.getDisplayName());

            joinQueueState.setJoinNotPossible(true)
                    .setJoinErrorMsg(msg);
        }

        if (!joinQueueState.isJoinNotPossible() && isTokenNotAvailable(jsonQueue)) {
            LocalTime tokenAvailableFrom = Formatter.parseLocalTime(Formatter.formatMilitaryTime(jsonQueue.getTokenAvailableFrom()));
            LocalTime tokenNotAvailableFrom = Formatter.parseLocalTime(Formatter.formatMilitaryTime(jsonQueue.getTokenNotAvailableFrom()));

            DateTime now = DateTime.now();
            LocalDate today = LocalDate.now();
            DateTime todayTokenAvailableFrom = today.toDateTime(tokenAvailableFrom);
            DateTime todayTokenNotAvailableFrom = today.toDateTime(tokenNotAvailableFrom);

            Log.d(TAG, "Token not available found as now " + now
                    + " is before " + todayTokenAvailableFrom
                    + " or after " + todayTokenNotAvailableFrom);
            joinQueueState.setJoinNotPossible(true);

            if (now.isBefore(todayTokenAvailableFrom)) {
                String startTime = Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom());
                String msg = String.format(
                        context.getString(R.string.error_token_available_from),
                        jsonQueue.getBusinessName(),
                        jsonQueue.getDisplayName(),
                        startTime);

                joinQueueState.setJoinErrorMsg(msg);
            } else if (now.isAfter(todayTokenNotAvailableFrom)) {
                String msg = String.format(context.getString(R.string.error_token_not_available_from), jsonQueue.getBusinessName(), jsonQueue.getDisplayName());
                joinQueueState.setJoinErrorMsg(msg);
            }
        }

        if (!joinQueueState.isJoinNotPossible() && isQueueClosedPermanently(jsonQueue)) {
            Log.d(TAG, "Queue closed permanently found");
            String msg = String.format(
                    context.getString(R.string.error_business_closed_permanent),
                    jsonQueue.getBusinessName(),
                    jsonQueue.getDisplayName());

            joinQueueState.setJoinNotPossible(true)
                    .setJoinErrorMsg(msg);
        }

        /* This should prevent unregistered client from joining. This condition should enforce client has to be logged in. */
        if (!joinQueueState.isJoinNotPossible() && jsonQueue.isAllowLoggedInUser()) {
            if (!UserUtils.isLogin()) {
                Log.d(TAG, "Queue can be joined by logged in user");
                String msg = String.format(context.getString(R.string.error_user_needs_to_be_logged_in), jsonQueue.getBusinessName(), jsonQueue.getDisplayName());
                joinQueueState.setJoinNotPossible(true)
                        .setJoinErrorMsg(msg);
            }
        }

        // Check againts for limited tokens
        if (!joinQueueState.isJoinNotPossible() && (jsonQueue.getAvailableTokenCount() > 0 && jsonQueue.getLastNumber() >= jsonQueue.getAvailableTokenCount())) {
            Log.d(TAG, "Queue token for the day is exhausted");
            String msg = context.getString(R.string.error_reached_daily_limit);
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
        LocalTime tokenAvailableFrom = Formatter.parseLocalTime(Formatter.formatMilitaryTime(jsonQueue.getTokenAvailableFrom()));
        LocalTime tokenNotAvailableFrom = Formatter.parseLocalTime(Formatter.formatMilitaryTime(jsonQueue.getTokenNotAvailableFrom()));

        DateTime now = DateTime.now();
        LocalDate today = LocalDate.now();
        DateTime todayTokenAvailableFrom = today.toDateTime(tokenAvailableFrom);
        DateTime todayTokenNotAvailableFrom = today.toDateTime(tokenNotAvailableFrom);

        return now.isBefore(todayTokenAvailableFrom) || now.isAfter(todayTokenNotAvailableFrom.plusMinutes(jsonQueue.getDelayedInMinutes()));
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
}
