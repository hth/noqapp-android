package com.noqapp.android.client.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.util.Date;

/**
 * User: chandra
 * Date: 5/1/17 7:11 PM
 */
public class Formatter {
    private static final DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis();

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

    public static DateTime getDateTimeFromString(String dateTimeString) {
        return parser2.parseDateTime(dateTimeString);
    }

    public static Date getDateFromString(String dateTimeString) {
        return getDateTimeFromString(dateTimeString).toDate();
    }

    public static String getDateAsString(Date date) {
        return DateFormat.getDateInstance().format(date);
    }
}
