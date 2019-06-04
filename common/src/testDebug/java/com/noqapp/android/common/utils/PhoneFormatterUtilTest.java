package com.noqapp.android.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PhoneFormatterUtilTest {

    @Test
    void phoneNumberWithCountryCode() {
        assertEquals("919812345678", PhoneFormatterUtil.phoneNumberWithCountryCode("09812345678", "IN"));
        assertEquals("919812345678", PhoneFormatterUtil.phoneNumberWithCountryCode("9812345678", "IN"));
        assertEquals("919812345678", PhoneFormatterUtil.phoneNumberWithCountryCode("919812345678", "IN"));
    }
}