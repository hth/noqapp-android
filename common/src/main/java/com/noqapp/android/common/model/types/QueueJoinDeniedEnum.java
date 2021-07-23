package com.noqapp.android.common.model.types;

/**
 * hitender
 * 10/28/20 5:08 PM
 */
public enum QueueJoinDeniedEnum {

    /* These should not be used for FCM messages. These needs to be fixed in a separate enum. */
    A("A", "Token issued after store closing"),

    /* B is when user joins with a different time on mobile. */
    B("B", "Token issued before store opening"),

    /* When store is closed. */
    C("C", "Store is closed"),

    W("W", "Cancelled service. Please wait until service has begun to reclaim your spot if available."),
    X("X", "You have been serviced in past. Please wait until few days to issue tokens."),
    T("T", "You have been served today"),
    L("L", "Reached maximum number of token");

    private final String name;
    private final String description;

    QueueJoinDeniedEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
