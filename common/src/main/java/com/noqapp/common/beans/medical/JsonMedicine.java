package com.noqapp.common.beans.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.beans.AbstractDomain;
import com.noqapp.common.model.types.MedicationTypeEnum;
import com.noqapp.common.model.types.MedicationWithFoodEnum;

import java.io.Serializable;

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
public class JsonMedicine extends AbstractDomain implements Serializable {

    @JsonProperty("na")
    private String name;

    @JsonProperty("st")
    private int strength;

    @JsonProperty("df")
    private int dailyFrequency;

    @JsonProperty("du")
    private int duration;

    @JsonProperty("mf")
    private MedicationWithFoodEnum medicationWithFood;

    @JsonProperty("mt")
    private MedicationTypeEnum medicationType;

    public String getName() {
        return name;
    }

    public JsonMedicine setName(String name) {
        this.name = name;
        return this;
    }

    public int getStrength() {
        return strength;
    }

    public JsonMedicine setStrength(int strength) {
        this.strength = strength;
        return this;
    }

    public int getDailyFrequency() {
        return dailyFrequency;
    }

    public JsonMedicine setDailyFrequency(int dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public JsonMedicine setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public MedicationWithFoodEnum getMedicationWithFood() {
        return medicationWithFood;
    }

    public JsonMedicine setMedicationWithFood(MedicationWithFoodEnum medicationWithFood) {
        this.medicationWithFood = medicationWithFood;
        return this;
    }

    public MedicationTypeEnum getMedicationType() {
        return medicationType;
    }

    public JsonMedicine setMedicationType(MedicationTypeEnum medicationType) {
        this.medicationType = medicationType;
        return this;
    }
}