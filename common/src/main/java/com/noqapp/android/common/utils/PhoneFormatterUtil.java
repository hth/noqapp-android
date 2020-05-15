package com.noqapp.android.common.utils;

import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.lang3.RegExUtils;

import android.util.Log;

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
            Log.e(TAG, "Parsing phone=" + rawPhoneNumber + " countryShortName=" + countryShortName + " reason=" + e.getLocalizedMessage(), e);
            FirebaseCrashlytics.getInstance().log("Parsing phone for validation " + rawPhoneNumber + " " + countryShortName);
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
            Log.e(TAG, "Parsing phone=" + rawPhoneNumber + " countryShortName=" + countryShortName + " reason=" + e.getLocalizedMessage(), e);
            FirebaseCrashlytics.getInstance().log("Parsing phone " + rawPhoneNumber + " " + countryShortName);
            return "";
        }
    }

    /**
     * From country short code like "US" returns country dial code like "1".
     *
     * @param countryShortCode
     * @return
     */
    public static int findCountryCodeFromCountryShortCode(String countryShortCode) {
        return phoneUtil.getCountryCodeForRegion(countryShortCode.toUpperCase());
    }

    public static String phoneNumberWithCountryCode(String phone, String countryShortName) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, countryShortName);
            Log.i(TAG, "PhoneNumber with phone=" + phone + " countryShortName=" + countryShortName
                    + " countryCode=" + phoneNumber.getCountryCode() + " nationalNumber=" + phoneNumber.getNationalNumber()
                    + " leadingZeros=" + phoneNumber.getNumberOfLeadingZeros());

            return phoneNumber.getCountryCode() + String.valueOf(phoneNumber.getNationalNumber());
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse phone=" + phone + " countryShortName=" + countryShortName + " reason=" + e.getLocalizedMessage(), e);
            FirebaseCrashlytics.getInstance().log("Failed parsing country code " + phone + " " + countryShortName);
            throw new RuntimeException("Failed parsing country code");
        }
    }

    public static int getCountryCodeFromRegion(String regionCode) {
        return phoneUtil.getCountryCodeForRegion(regionCode.toUpperCase());
    }

    /**
     * Removes country code from phone.
     * Note: Do not pass a Raw Phone as it will strip few digits.
     *
     * @param phone should begin with +
     * @return
     */
    public static String phoneStripCountryCode(String phone) {
        try {
            //assertThat(phone, containsString("+"));
            /* Remove `+` sign with country code before sending the number back. */
            return RegExUtils.removeFirst(phone, "\\+" + findCountryCode(phone));
        } catch (AssertionError a) {
            Log.w(TAG, "Phone number should begin with + phone " + phone);
            try {
                return phoneStripCountryCode("+" + phone);
            } catch (Exception e) {
                Log.e(TAG, "Failed getting country code from phone " + e.getLocalizedMessage(), e);
                throw new RuntimeException("Failed finding country code from phone");
            }
        }
    }

    /**
     * Parse for country code from phone.
     *
     * @param phone should begin with +
     * @return
     */
    public static int findCountryCode(String phone) {
        try {
            //assertThat(phone, containsString("+"));
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, "");
            return phoneNumber.getCountryCode();
        } catch (NumberParseException e) {
            Log.e(TAG, "Failed to parse phone " + e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed parsing country code");
        }
    }
}
