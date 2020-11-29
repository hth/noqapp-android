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
        if (time < 10 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.few_minutes);
        } else if (time < 50 * MINUTE_MILLIS) {
            return String.format(context.getResources().getString(R.string.approx_minutes), (time / MINUTE_MILLIS));
        } else if (time < 75 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.approx_an_hour);
        } else if (time < 135 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.approx_two_hours);
        } else if (time < 24 * HOUR_MILLIS) {
            return String.format(context.getResources().getString(R.string.approx_hours), (time / HOUR_MILLIS));
        } else if (time < 48 * HOUR_MILLIS) {
            return context.getResources().getString(R.string.more_than_a_day);
        } else {
            return String.format(context.getResources().getString(R.string.approx_days), (time / DAY_MILLIS));
        }
    }

    public static String getTimeInAgo(long time) {
        if (time == 0) {
            return null;
        }
        // TODO: localize
        if (time < MINUTE_MILLIS) {
            return time / 1000 + " seconds ago";
        } else if (time < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (time < 50 * MINUTE_MILLIS) {
            return time / MINUTE_MILLIS + " minutes ago";
        } else if (time < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (time < 24 * HOUR_MILLIS) {
            return time / HOUR_MILLIS + " hours ago";
        } else if (time < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return time / DAY_MILLIS + " days ago";
        }
    }
}
