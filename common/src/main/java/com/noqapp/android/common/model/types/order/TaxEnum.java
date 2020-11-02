package com.noqapp.android.common.model.types.order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 10/8/20 11:03 AM
 */
public enum TaxEnum {
    ZE("ZE", "0%", new BigDecimal(0)),
    PT("PT", "0.25%", new BigDecimal(25).movePointLeft(2)),
    TH("TH", "3%", new BigDecimal(3)),
    FI("FI", "5%", new BigDecimal(5)),
    TW("TW", "12%", new BigDecimal(12)),
    ET("ET", "18%", new BigDecimal(18)),
    TE("TE", "28%", new BigDecimal(28));

    private final String name;
    private final String description;
    private final BigDecimal value;

    TaxEnum(String name, String description, BigDecimal value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public static List<TaxEnum> asList() {
        TaxEnum[] all = TaxEnum.values();
        return Arrays.asList(all);
    }

    public static List<BigDecimal> asListOfDescription() {
        List<BigDecimal> a = new LinkedList<>();
        for (TaxEnum tax : TaxEnum.values()) {
            a.add(tax.value);
        }
        return a;
    }

    public static List<String> asListOfDescriptionAsString() {
        List<String> a = new LinkedList<>();
        for (TaxEnum tax : TaxEnum.values()) {
            a.add(tax.description);
        }
        return a;
    }

    public static TaxEnum getEnum(String description) {
        for (TaxEnum unitOfMeasurementEnum : TaxEnum.values()) {
            if (description.equals(unitOfMeasurementEnum.description)) {
                return unitOfMeasurementEnum;
            }
        }
        return TaxEnum.ZE;
    }

    @Override
    public String toString() {
        return description;
    }
}
