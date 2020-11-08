package com.noqapp.android.common.model.types;

/**
 * Store accepted payment.
 * hitender
 * 11/7/20 9:51 AM
 */
public enum SupportedPaymentEnum {
    COD("COD", "Cash on Delivery", "COD"),
    ONP("ONP", "Online Payment", "Online");

    private final String name;
    private final String description;
    private final String friendlyDescription;

    SupportedPaymentEnum(String name, String description, String friendlyDescription) {
        this.name = name;
        this.description = description;
        this.friendlyDescription = friendlyDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFriendlyDescription() {
        return friendlyDescription;
    }

    @Override
    public String toString() {
        return description;
    }
}
