package com.noqapp.client.helper;

import android.util.Log;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * User: hitender
 * Date: 5/1/17 12:08 PM
 */

public class PhoneFormatterUtil {
    private static final String TAG = PhoneFormatterUtil.class.getSimpleName();
    private static PhoneNumberUtil phoneUtil;

    static {
        phoneUtil = PhoneNumberUtil.getInstance();
    }

    public static boolean isValid(String countryShortName, String rawPhoneNumber) {
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(rawPhoneNumber, countryShortName);
            return phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            Log.e(TAG, "Parsing phone=" + rawPhoneNumber + " CS=" + countryShortName + " reason=" + e.getLocalizedMessage(), e);
            return false;
        }
    }

    public static String formatAsYouType(String countryShortName, String singleDigit) {
        AsYouTypeFormatter formatter = phoneUtil.getAsYouTypeFormatter(countryShortName);
        //return formatter.inputDigit(singleDigit);
        return singleDigit;
    }

    public static String formatNumber(String countryShortName, String rawPhoneNumber) {
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(rawPhoneNumber, countryShortName);
            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            Log.e(TAG, "Parsing phone=" + rawPhoneNumber + " CS=" + countryShortName + " reason=" + e.getLocalizedMessage(), e);
            return "";
        }
    }
}
