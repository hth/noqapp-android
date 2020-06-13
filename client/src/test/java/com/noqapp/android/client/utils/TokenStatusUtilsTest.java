package com.noqapp.android.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenStatusUtilsTest {

    @Test
    void timeSlot() {
        String slot = TokenStatusUtils.timeSlot("2020-06-13T11:23:57.143+0000");
        assertEquals("time slot between 4:00 - 5:00", slot);

        slot = TokenStatusUtils.timeSlot("2020-06-13T11:45:57.143+0000");
        assertEquals("time slot between 4:30 - 5:30", slot);

        slot = TokenStatusUtils.timeSlot("2020-06-13T11:13:57.143+0000");
        assertEquals("time slot between 3:30 - 4:30", slot);
    }
}