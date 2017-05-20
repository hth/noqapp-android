package com.noqapp.android.merchant.model.types;

/**
 * Created by chandra on 4/23/17.
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

