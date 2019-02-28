package com.noqapp.android.common.model.types.order;

/**
 * Created by hitender on 3/27/18.
 */

public enum DeliveryModeEnum {
    HD("HD", "Home Delivery"),
    TO("TO", "Takeaway");

    private final String description;
    private final String name;

    DeliveryModeEnum(String name, String description) {
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
