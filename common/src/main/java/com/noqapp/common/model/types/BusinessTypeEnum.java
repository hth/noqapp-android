package com.noqapp.common.model.types;

/**
 * Created by hitender on 1/2/18.
 */

public enum BusinessTypeEnum {
    RS("RS", "Restaurant"),
    BA("BA", "Bar"),
    ST("ST", "Store"),
    LD("LD", "Lodging"),
    SM("SM", "Shopping Mall"),
    MT("MT", "Movie Theater"),
    GA("GA", "Gas Station"),
    SC("SC", "School"),
    GS("GS", "Grocery Store"),
    CF("CF", "Cafe"),
    DO("DO", "Hospital/Doctor"),
    PH("PH", "Pharmacy"),
    PW("PW", "Place of Worship"),
    MU("MU", "Museum"),
    TA("TA", "Tourist Attraction"),
    NC("NC", "Night Club"),
    BK("BK", "Bank"),
    AT("AT", "ATM"),
    GY("GY", "GYM"),
    PA("PA", "Park");

    private final String description;
    private final String name;

    BusinessTypeEnum(String name, String description) {
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
