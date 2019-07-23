package com.noqapp.android.common.model.types;

/**
 * User: hitender
 * Date: 2019-05-20 14:11
 */
public enum AdvertisementViewerTypeEnum {
    WTC("WTC", "With Terms And Conditions"),
    JBA("JBA", "Just Banner");

    private final String description;
    private final String name;

    AdvertisementViewerTypeEnum(String name, String description) {
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