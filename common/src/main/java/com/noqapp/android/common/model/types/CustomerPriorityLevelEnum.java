package com.noqapp.android.common.model.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hitender
 * 5/24/20 12:25 PM
 */
public enum CustomerPriorityLevelEnum {
    S("S", "Silver (1)", 1, "B1D877"),
    G("G", "Gold (2)", 2, "8CDCDA"),
    P("P", "Platinum (3)", 3, "F16A70"),
    R("R", "Rare Metal (Highest)", 4, "4D4D4D");

    private final String description;
    private final String name;
    private final int level;
    private final String colorCode;

    CustomerPriorityLevelEnum(String name, String description, int level, String colorCode) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.colorCode = colorCode;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getColorCode() {
        return colorCode;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(S.name, S.description);
            put(G.name, G.description);
            put(P.name, P.description);
            put(R.name, R.description);
        }};
    }

    public static CustomerPriorityLevelEnum getCustomerPriorityLevelEnumBasedOnLevel(int level) {
        switch (level) {
            case 1:
                return S;
            case 2:
                return G;
            case 3:
                return P;
            case 4:
                return R;
            default:
                throw new UnsupportedOperationException("Reached Unsupported Condition");
        }
    }

    @Override
    public String toString() {
        return description;
    }
}