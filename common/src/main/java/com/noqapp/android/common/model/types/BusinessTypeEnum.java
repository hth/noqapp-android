package com.noqapp.android.common.model.types;

import static com.noqapp.android.common.model.types.QueueOrderTypeEnum.O;
import static com.noqapp.android.common.model.types.QueueOrderTypeEnum.Q;

import java.io.Serializable;

/**
 * Created by hitender on 1/2/18.
 */

public enum BusinessTypeEnum implements Serializable {
    RS("RS", "Restaurant", O),
    BA("BA", "Bar", O),
    ST("ST", "Store", O),
    SM("SM", "Shopping Mall", Q),
    MT("MT", "Movie Theater", Q),
    SC("SC", "School", Q),
    GS("GS", "Grocery Store", O),
    CF("CF", "Cafe", O),
    DO("DO", "Hospital/Doctor", Q),
    PH("PH", "Pharmacy", O),
    PW("PW", "Place of Worship", Q),
    MU("MU", "Museum", Q),
    TA("TA", "Tourist Attraction", Q),
    NC("NC", "Night Club", Q),
    BK("BK", "Bank", Q),
    PA("PA", "Park", Q);

    private final String description;
    private final String name;
    private final QueueOrderTypeEnum queueOrderType;

    BusinessTypeEnum(String name, String description, QueueOrderTypeEnum queueOrderType) {
        this.name = name;
        this.description = description;
        this.queueOrderType = queueOrderType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QueueOrderTypeEnum getQueueOrderType() {
        return queueOrderType;
    }

    @Override
    public String toString() {
        return description;
    }
}
