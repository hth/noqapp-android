package com.noqapp.android.common.beans;

import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2019-02-08 11:42
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
public class JsonNameDateHealth extends JsonNameDatePair {

    @JsonProperty("hs")
    private HealthCareServiceEnum healthCareService;

    public HealthCareServiceEnum getHealthCareService() {
        return healthCareService;
    }

    public JsonNameDateHealth setHealthCareService(HealthCareServiceEnum healthCareService) {
        this.healthCareService = healthCareService;
        return this;
    }

    @Override
    public String toString() {
        return "JsonNameDateHealth{" +
                "healthCareService=" + healthCareService +
                '}';
    }
}
