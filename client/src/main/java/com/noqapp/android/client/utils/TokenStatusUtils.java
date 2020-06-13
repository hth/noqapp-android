package com.noqapp.android.client.utils;

import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.utils.Formatter;

import org.joda.time.LocalTime;

/**
 * This is a utile class for token status.
 */
public class TokenStatusUtils {
    private static final String TAG = TokenStatusUtils.class.getSimpleName();

    // Private constructor
    private TokenStatusUtils() {
    }

    /**
     * Calculate the estimated wait time
     *
     * @param avgServiceTime  Average handling time for a token
     * @param positionInQueue assigned token position in queue
     * @param queueStatus     current status of queue
     * @return the estimated wait time
     */
    public static String calculateEstimatedWaitTime(
        long avgServiceTime,
        int positionInQueue,
        QueueStatusEnum queueStatus,
        int startHour
    ) {
        if (avgServiceTime > 0 && positionInQueue > 0) {
            if (queueStatus == QueueStatusEnum.S) {
                long timeToStoreStartInMilli = Formatter.computeTimeToStoreStart(startHour);
                if (timeToStoreStartInMilli > 0) {
                    return GetTimeAgoUtils.getTimeAgo(positionInQueue * avgServiceTime + timeToStoreStartInMilli);
                } else {
                    return GetTimeAgoUtils.getTimeAgo(positionInQueue * avgServiceTime);
                }
            } else {
                return GetTimeAgoUtils.getTimeAgo(positionInQueue * avgServiceTime);
            }
        }
        return null;
    }

    public static String timeSlot(String date) {
        String expectedServiceTime = Formatter.getTimeMilitary(date);
        LocalTime localTime = Formatter.parseLocalTime(expectedServiceTime);

        int minutes = localTime.getMinuteOfHour();
        if (minutes >= 45) {
            LocalTime before = localTime.minusMinutes(minutes).plusMinutes(30);
            LocalTime after = before.plusHours(1);
            return "time slot between " + before.getHourOfDay() + ":" + before.getMinuteOfHour() + " - " + after.getHourOfDay() + ":" + after.getMinuteOfHour();
        } else if (minutes <= 15) {
            LocalTime before = localTime.minusMinutes(minutes).minusMinutes(30);
            LocalTime after = before.plusHours(1);
            return "time slot between " + before.getHourOfDay() + ":" + before.getMinuteOfHour() + " - " + after.getHourOfDay() + ":" + after.getMinuteOfHour();
        } else {
            LocalTime after = localTime.plusHours(1);
            return "time slot between " + localTime.getHourOfDay() + ":" + "00" + " - " + after.getHourOfDay() + ":" + "00";
        }
    }
}
