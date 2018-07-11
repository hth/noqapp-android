package com.noqapp.android.common.beans.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.MedicationTypeEnum;
import com.noqapp.android.common.model.types.MedicationWithFoodEnum;

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
public class JsonMedicalMedicine extends AbstractDomain implements Serializable {

    @JsonProperty("na")
    private String name;

    @JsonProperty("st")
    private String strength;

    @JsonProperty("df")
    private String dailyFrequency;

    @JsonProperty("co")
    private String course;

    @JsonProperty("mf")
    private MedicationWithFoodEnum medicationWithFood;

    @JsonProperty("mt")
    private MedicationTypeEnum medicationType;

    public String getName() {
        return name;
    }

    public JsonMedicalMedicine setName(String name) {
        this.name = name;
        return this;
    }

    public String getStrength() {
        return strength;
    }

    public JsonMedicalMedicine setStrength(String strength) {
        this.strength = strength;
        return this;
    }

    public String getDailyFrequency() {
        return dailyFrequency;
    }

    public JsonMedicalMedicine setDailyFrequency(String dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
        return this;
    }

    public String getCourse() {
        return course;
    }

    public JsonMedicalMedicine setCourse(String course) {
        this.course = course;
        return this;
    }

    public MedicationWithFoodEnum getMedicationWithFood() {
        return medicationWithFood;
    }

    public JsonMedicalMedicine setMedicationWithFood(MedicationWithFoodEnum medicationWithFood) {
        this.medicationWithFood = medicationWithFood;
        return this;
    }

    public MedicationTypeEnum getMedicationType() {
        return medicationType;
    }

    public JsonMedicalMedicine setMedicationType(MedicationTypeEnum medicationType) {
        this.medicationType = medicationType;
        return this;
    }
}