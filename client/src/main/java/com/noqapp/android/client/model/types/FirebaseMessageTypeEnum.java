package com.noqapp.android.client.model.types;

/**
 * User: hitender
 * Date: 5/7/17 8:21 AM
 */

public enum FirebaseMessageTypeEnum {

    C("C", "Client"),
    M("M", "Merchant"),
    P("P", "Personal");

    private final String description;
    private final String name;

    FirebaseMessageTypeEnum(String name, String description) {
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
