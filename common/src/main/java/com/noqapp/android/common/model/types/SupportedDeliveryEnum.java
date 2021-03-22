package com.noqapp.android.common.model.types;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Store accepted delivery.
 * hitender
 * 11/7/20 9:53 AM
 */
public enum SupportedDeliveryEnum {
    HOM("HOM", "Home Delivery"),
    PIK("PIK", "Pick-up");

    private final String name;
    private final String description;

    SupportedDeliveryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Map<String, String> asMapWithNameAsKey(Set<SupportedDeliveryEnum> supportedDeliveries) {
        HashMap<String, String> availableOptions = new LinkedHashMap<>();
        for (SupportedDeliveryEnum supportedDelivery : supportedDeliveries) {
            availableOptions.put(supportedDelivery.name, supportedDelivery.description);
        }
        return availableOptions;
    }

    @Override
    public String toString() {
        return description;
    }
}
