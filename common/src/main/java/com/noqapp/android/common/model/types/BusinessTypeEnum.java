package com.noqapp.android.common.model.types;

import static com.noqapp.android.common.model.types.BusinessSupportEnum.OD;
import static com.noqapp.android.common.model.types.BusinessSupportEnum.OQ;
import static com.noqapp.android.common.model.types.BusinessSupportEnum.QQ;
import static com.noqapp.android.common.model.types.QueueOrderTypeEnum.O;
import static com.noqapp.android.common.model.types.QueueOrderTypeEnum.Q;

import java.io.Serializable;

/**
 * Created by hitender on 1/2/18.
 */
public enum BusinessTypeEnum implements Serializable {
    RS("RS", "Restaurant", O, "Store", OD),
    RSQ("RSQ", "Restaurant (Queue Only)", Q, "Store", OQ),

    FT("FT", "Food Truck", O, "Store", OD),
    FTQ("FTQ", "Food Truck (Queue Only)", Q, "Store", OQ),

    BA("BA", "Bar", O, "Store", OD),
    BAQ("BAQ", "Bar (Queue Only)", Q, "Store", OQ),

    ST("ST", "Generic Store", O, "Store", OD),
    STQ("STQ", "Generic Store (Queue Online)", Q, "Store", OQ),

    GS("GS", "Grocery Store", O, "Store", OD),
    GSQ("GSQ", "Grocery Store (Queue Only)", Q, "Store", OQ),

    CF("CF", "Cafeteria", O, "Store", OD),
    CFQ("CFQ", "Cafeteria (Queue Online)", Q, "Store", OQ),

    SM("SM", "Shopping Mall", Q, "Queue", QQ),
    MT("MT", "Movie Theater", Q, "Queue", QQ),
    SC("SC", "School", Q, "Queue", QQ),
    DO("DO", "Hospital/Doctor", Q, "Queue", QQ),
    HS("HS", "Health Care Services", O, "Store", OD),
    PH("PH", "Pharmacy", O, "Store", OD),                //Users cannot directly order these, as these have to be prescribed
    PW("PW", "Place of Worship", Q, "Queue", QQ),
    MU("MU", "Museum", Q, "Queue", QQ),
    TA("TA", "Tourist Attraction", Q, "Queue", QQ),
    NC("NC", "Night Club", Q, "Queue", QQ),
    BK("BK", "Bank", Q, "Queue", QQ),
    PA("PA", "Park", Q, "Queue", QQ);

    private final String description;
    private final String name;
    private final QueueOrderTypeEnum queueOrderType;
    private final String classifierTitle;
    private final BusinessSupportEnum businessSupport;

    BusinessTypeEnum(String name, String description, QueueOrderTypeEnum queueOrderType, String classifierTitle, BusinessSupportEnum businessSupport) {
        this.name = name;
        this.description = description;
        this.queueOrderType = queueOrderType;
        this.classifierTitle = classifierTitle;
        this.businessSupport = businessSupport;
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

    public BusinessSupportEnum getBusinessSupport() {
        return businessSupport;
    }

    @Override
    public String toString() {
        return description;
    }
}
