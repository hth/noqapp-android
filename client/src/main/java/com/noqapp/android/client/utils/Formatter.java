package com.noqapp.android.client.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User: chandra
 * Date: 5/1/17 7:11 PM
 */
public class Formatter {
    private static final String TAG = Formatter.class.getSimpleName();
    private static final DateFormat formatRFC822 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

    private static final DateFormat df = DateFormat.getDateInstance();
    private static final TimeZone tz = TimeZone.getTimeZone("UTC");

    private Formatter() {
        formatRFC822.setTimeZone(tz);
    }

    public static String getFormattedAddress(String address) {
        if (address.contains(",")) {
            String[] arr = address.split(",");
            if (arr.length > 2) {
                int secondIndex = address.indexOf(',', address.indexOf(',') + 1);
                return address.substring(0, secondIndex + 1) + "\n" + address.substring(secondIndex + 1, address.length());
            } else
                return address;
        } else {
            return address;
        }
    }

    public static Date getDateFromString(String dateTimeString) {
        try {
            return formatRFC822.parse(dateTimeString);
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error reason" + e.getLocalizedMessage());
        }

        return null;
    }

    public static String getDateTimeAsString(Date date) {
        if (null != date) {
            return DateFormat.getDateTimeInstance().format(date);
        }

        return "";
    }
}
