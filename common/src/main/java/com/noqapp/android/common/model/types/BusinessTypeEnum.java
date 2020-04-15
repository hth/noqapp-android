package com.noqapp.android.common.model.types;

import static com.noqapp.android.common.model.types.QueueOrderTypeEnum.O;
import static com.noqapp.android.common.model.types.QueueOrderTypeEnum.Q;

import java.io.Serializable;

/**
 * Created by hitender on 1/2/18.
 */
public enum BusinessTypeEnum implements Serializable {
    RS("RS", "Restaurant", O, "Store"),
    FT("FT", "Food Truck", O, "Store"),
    BA("BA", "Bar", O, "Store"),
    ST("ST", "Store", O, "Store"),
    SM("SM", "Shopping Mall", Q, "Queue"),
    MT("MT", "Movie Theater", Q, "Queue"),
    SC("SC", "School", Q, "Queue"),
    GS("GS", "Grocery Store", Q, "Queue"),
    CF("CF", "Cafe", O, "Store"),
    DO("DO", "Hospital/Doctor", Q, "Queue"),
    HS("HS", "Health Care Services", O, "Store"),
    PH("PH", "Pharmacy", O, "Store"),                //Users cannot directly order these, as these have to be prescribed
    PW("PW", "Place of Worship", Q, "Queue"),
    MU("MU", "Museum", Q, "Queue"),
    TA("TA", "Tourist Attraction", Q, "Queue"),
    NC("NC", "Night Club", Q, "Queue"),
    BK("BK", "Bank", Q, "Queue"),
    PA("PA", "Park", Q, "Queue");

    private final String description;
    private final String name;
    private final QueueOrderTypeEnum queueOrderType;
    private final String classifierTitle;

    BusinessTypeEnum(String name, String description, QueueOrderTypeEnum queueOrderType, String classifierTitle) {
        this.name = name;
        this.description = description;
        this.queueOrderType = queueOrderType;
        this.classifierTitle = classifierTitle;
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

    public String getClassifierTitle() {
        return classifierTitle;
    }

    @Override
    public String toString() {
        return description;
    }
}
