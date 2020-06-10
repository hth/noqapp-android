package com.noqapp.android.common.model.types;

/**
 * User: hitender
 * Date: 6/9/17 9:31 AM
 */
public enum UserLevelEnum {
    CLIENT("Client", 10),
    Q_SUPERVISOR("Queue Supervisor", 22),
    A_SUPERVISOR("Asset Supervisor", 23),
    S_MANAGER("Store Manager", 24),
    M_ACCOUNTANT("Business Accountant", 26),
    M_ADMIN("Business Admin", 29),
    TECHNICIAN("Tech", 40),
    MEDICAL_TECHNICIAN("Medical Tech", 41),
    SUPERVISOR("Super", 50),
    ANALYSIS("Analysis", 60),
    ADMIN("Admin", 90);

    private final String description;
    private final int value;

    /**
     * @param description
     * @param value       - used for comparing specific access
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
