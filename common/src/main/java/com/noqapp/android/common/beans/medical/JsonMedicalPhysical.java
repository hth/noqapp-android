package com.noqapp.android.common.beans.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;

import java.io.Serializable;

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
public class JsonMedicalPhysical extends AbstractDomain implements Serializable {

    @JsonProperty("pl")
    private String pluse;

    @JsonProperty("bp")
    private String[] bloodPressure;

    @JsonProperty("wt")
    private String weight;

    public String getPluse() {
        return pluse;
    }

    public JsonMedicalPhysical setPluse(String pluse) {
        this.pluse = pluse;
        return this;
    }

    public String[] getBloodPressure() {
        return bloodPressure;
    }

    public JsonMedicalPhysical setBloodPressure(String[] bloodPressure) {
        this.bloodPressure = bloodPressure;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public JsonMedicalPhysical setWeight(String weight) {
        this.weight = weight;
        return this;
    }
}