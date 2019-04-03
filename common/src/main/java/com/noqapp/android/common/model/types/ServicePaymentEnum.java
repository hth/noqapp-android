package com.noqapp.android.common.model.types;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2019-03-13 11:45
 */
public enum ServicePaymentEnum {
    R("R", "Required"),
    O("O", "Optional");

    private final String name;
    private final String description;

    ServicePaymentEnum(String name, String description) {
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

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (ServicePaymentEnum servicePaymentEnum : ServicePaymentEnum.values()) {
            a.add(servicePaymentEnum.description);
        }
        return a;
    }


    public static ServicePaymentEnum getEnum(String description){
        ServicePaymentEnum temp = null;
        for (ServicePaymentEnum servicePaymentEnum : ServicePaymentEnum.values()) {
            if(servicePaymentEnum.description.equals(description)){
                return servicePaymentEnum;
            }
        }
        return temp;
    }
}