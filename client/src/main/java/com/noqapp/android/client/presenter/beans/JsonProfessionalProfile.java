package com.noqapp.android.client.presenter.beans;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 5/30/18 10:26 AM
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
public class JsonProfessionalProfile extends JsonProfessionalProfilePersonal {

    @JsonProperty("st")
    private List<JsonStore> stores = new ArrayList<>();

    public List<JsonStore> getStores() {
        return stores;
    }

    public JsonProfessionalProfile setStores(List<JsonStore> stores) {
        this.stores = stores;
        return this;
    }
}
