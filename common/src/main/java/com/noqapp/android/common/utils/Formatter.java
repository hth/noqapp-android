package com.noqapp.android.common.utils;

import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.util.Log;

import com.noqapp.android.common.model.types.QueueStatusEnum;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User: chandra
 * Date: 5/1/17 7:11 PM
 */
public class Formatter {
    public static final DateFormat formatRFC822 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
    private static final String TAG = Formatter.class.getSimpleName();
    private static final DateFormat df = DateFormat.getDateInstance();
    private static final TimeZone tz = TimeZone.getTimeZone("UTC");

    private static DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("HHmm");
    private static DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("hh:mm a");
    private static DateTimeFormatter outputFormatter24 = DateTimeFormat.forPattern("HH:mm");

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

    public static String getTimeAsString(Date date) {
        if (null != date) {
            return DateFormat.getTimeInstance().format(date);
        }

        return "";
    }

    public static String getTime(String inputDate) {
        try {
            DateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(formatRFC822.parse(inputDate));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static LocalTime parseLocalTime(String rawTimestamp) {
        return inputFormatter.parseLocalTime(rawTimestamp);
    }

    private static String convertMilitaryTo12HourFormat(String rawTimestamp) {
        return outputFormatter.print(parseLocalTime(rawTimestamp)).toUpperCase();
    }

    private static String convertMilitaryTo24HourFormat(String rawTimestamp) {
        return outputFormatter24.print(parseLocalTime(rawTimestamp));
    }

    public static String convertMilitaryTo24HourFormat(LocalTime localTime) {
        return outputFormatter24.print(localTime);
    }

    public static String formatMilitaryTime(int rawTimestamp) {
        return String.format(Locale.US, "%04d", rawTimestamp);
    }

    public static String convertMilitaryTo12HourFormat(int rawTimestamp) {
        return convertMilitaryTo12HourFormat(formatMilitaryTime(rawTimestamp));
    }

    public static String convertMilitaryTo24HourFormat(int rawTimestamp) {
        return convertMilitaryTo24HourFormat(formatMilitaryTime(rawTimestamp));
    }

    /**
     * Returns string format XX:XX AM - XX:XX PM
     * @param startHour
     * @param endHour
     * @return
     */
    public static String duration(int startHour, int endHour) {
        return Formatter.convertMilitaryTo12HourFormat(startHour) + " - " + Formatter.convertMilitaryTo12HourFormat(endHour);
    }

    public static LocalTime getLocalTime(int hourAndMinute) {
        return parseLocalTime(String.format(Locale.US, "%04d", hourAndMinute));
    }

    public static long computeEstimatedServiceTime(QueueStatusEnum queueStatus, String storeStart) {
        switch (queueStatus) {
            case S:
                return Seconds.secondsBetween(LocalTime.parse(storeStart), LocalTime.now()).getSeconds() * 1000;
            default:
                return Calendar.getInstance().getTimeInMillis();
        }
    }
}
