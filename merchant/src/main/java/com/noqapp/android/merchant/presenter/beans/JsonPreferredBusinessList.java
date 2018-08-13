package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 8/12/18 11:40 PM
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonPreferredBusinessList implements Serializable {
    @JsonProperty("pbs")
    private List<JsonPreferredBusiness> preferredBusinesses = new ArrayList<>();

    public List<JsonPreferredBusiness> getPreferredBusinesses() {
        return preferredBusinesses;
    }

    public JsonPreferredBusinessList setPreferredBusinesses(List<JsonPreferredBusiness> preferredBusinesses) {
        this.preferredBusinesses = preferredBusinesses;
        return this;
    }

    public JsonPreferredBusinessList addPreferredBusinesses(List<JsonPreferredBusiness> preferredBusinesses) {
        this.preferredBusinesses.addAll(preferredBusinesses);
        return this;
    }
}
