package com.noqapp.android.client.utils;


import android.content.Context;

import com.noqapp.android.client.R;

/**
 * Created by chandra on 6/4/17.
 */

public class GetTimeAgoUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context context) {

        if (time == 0) {
            return null;
        }
        final long diff = time;
        if (diff < MINUTE_MILLIS) {
            return String.format(context.getResources().getString(R.string.seconds), (diff / 1000));
        } else if (diff < 10 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.few_minutes);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return String.format(context.getResources().getString(R.string.approx_minutes), (diff / MINUTE_MILLIS ));
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.approx_an_hour);
        } else if (diff < 24 * HOUR_MILLIS) {
            return String.format(context.getResources().getString(R.string.approx_hours), (diff / HOUR_MILLIS ));
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getResources().getString(R.string.more_than_a_day);
        } else {
            return String.format(context.getResources().getString(R.string.approx_days), (diff / DAY_MILLIS ));
        }
    }

    public static String getTimeInAgo(long time) {

        if (time == 0) {
            return null;
        }
        // TODO: localize
        final long diff = time;
        if (diff < MINUTE_MILLIS) {
            return diff / 1000 + " seconds ago";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Tomorrow";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
