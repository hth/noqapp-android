package com.noqapp.android.merchant.presenter.beans.stats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 6/6/18 3:26 PM
 */
@SuppressWarnings ({
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthCareStatList {

    @JsonProperty("hcs")
    private List<HealthCareStat> healthCareStat = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<HealthCareStat> getHealthCareStat() {
        return healthCareStat;
    }

    public HealthCareStatList setHealthCareStat(List<HealthCareStat> healthCareStat) {
        this.healthCareStat = healthCareStat;
        return this;
    }

    public HealthCareStatList addHealthCareStat(HealthCareStat healthCareStat) {
        this.healthCareStat.add(healthCareStat);
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HealthCareStatList{");
        sb.append("healthCareStat=").append(healthCareStat);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
