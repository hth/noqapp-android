package com.noqapp.android.client.model.types;

/**
 * User: hitender
 * Date: 6/9/17 9:31 AM
 */
public enum UserLevelEnum {
    CLIENT("Client", 10),
    MER_MANAGER("Merchant Manager", 20),
    MER_ADMIN("Merchant Admin", 30),
    TECHNICIAN("Tech", 40),
    SUPERVISOR("Super", 50),
    ANALYSIS("Analysis", 60),
    ADMIN("Admin", 90);

    private final String description;
    private final int value;

    /**
     *
     * @param description
     * @param value - used for comparing specific access
     */
    UserLevelEnum(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description;
    }
}
