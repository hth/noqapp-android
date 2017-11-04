package com.noqapp.android.client.utils;


/**
 * Created by chandra on 6/4/17.
 */

public class GetTimeAgoUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {

        if (time == 0) {
            return null;
        }
        // TODO: localize
        final long diff = time;
        if (diff < MINUTE_MILLIS) {
            return "In "+diff/1000+" seconds";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "In a minute";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "In " + diff / MINUTE_MILLIS + " minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "In an hour";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "In " + diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Tomorrow";
        } else {
            return "In " + diff / DAY_MILLIS + " days";
        }
    }
}
