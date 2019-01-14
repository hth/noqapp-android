package com.noqapp.android.common.beans.medical;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Arrays;

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

    @JsonProperty("te")
    private String temperature;

    @JsonProperty("pl")
    private String pulse;

    @JsonProperty("bp")
    private String[] bloodPressure;

    @JsonProperty("ox")
    private String oxygen;

    @JsonProperty("rp")
    private String respiratory;

    @JsonProperty("wt")
    private String weight;

    @JsonProperty("ht")
    private String height;

    @JsonProperty("dbi")
    private String diagnosedById;

    public String getTemperature() {
        return temperature;
    }

    public JsonMedicalPhysical setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getPulse() {
        return pulse;
    }

    public JsonMedicalPhysical setPulse(String pulse) {
        this.pulse = pulse;
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

    public String getHeight() {
        return height;
    }

    public JsonMedicalPhysical setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getOxygen() {
        return oxygen;
    }

    public JsonMedicalPhysical setOxygen(String oxygen) {
        this.oxygen = oxygen;
        return this;
    }

    public String getRespiratory() {
        return respiratory;
    }

    public JsonMedicalPhysical setRespiratory(String respiratory) {
        this.respiratory = respiratory;
        return this;
    }

    public String getDiagnosedById() {
        return diagnosedById;
    }

    public JsonMedicalPhysical setDiagnosedById(String diagnosedById) {
        this.diagnosedById = diagnosedById;
        return this;
    }

    @Override
    public String toString() {
        return "JsonMedicalPhysical{" +
                "temperature='" + temperature + '\'' +
                ", pulse='" + pulse + '\'' +
                ", bloodPressure=" + Arrays.toString(bloodPressure) +
                ", oxygen='" + oxygen + '\'' +
                ", respiratory='" + respiratory + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                ", diagnosedById='" + diagnosedById + '\'' +
                '}';
    }
}