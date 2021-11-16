package com.noqapp.android.common.model.types.category;

/**
 * hitender
 * 11/7/21 7:47 AM
 */
public enum MarketplaceRejectReasonEnum {
    ADMD("ADMD", "Add More Details"),
    ADIM("ADIM", "Add Image"),
    URIS("URIS", "User Reported Issue");

    private final String name;
    private final String description;

    MarketplaceRejectReasonEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
