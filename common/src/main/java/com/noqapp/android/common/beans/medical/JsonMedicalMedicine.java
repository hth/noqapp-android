package com.noqapp.android.common.beans.medical;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Objects;

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
    private String medicationWithFood;

    @JsonProperty("pc")
    private String pharmacyCategory;

    private boolean isFavourite = false;

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

    public String getMedicationWithFood() {
        return medicationWithFood;
    }

    public JsonMedicalMedicine setMedicationWithFood(String medicationWithFood) {
        this.medicationWithFood = medicationWithFood;
        return this;
    }

    public String getPharmacyCategory() {
        return pharmacyCategory;
    }

    public JsonMedicalMedicine setPharmacyCategory(String pharmacyCategory) {
        this.pharmacyCategory = pharmacyCategory;
        return this;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public JsonMedicalMedicine setFavourite(boolean favourite) {
        isFavourite = favourite;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof JsonMedicalMedicine)) {
            return false;
        }
        JsonMedicalMedicine user = (JsonMedicalMedicine) o;
        return
                Objects.equals(name, user.name) &&
                        Objects.equals(pharmacyCategory, user.pharmacyCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, strength, dailyFrequency, course, medicationWithFood, pharmacyCategory, isFavourite);
    }

    @Override
    public String toString() {
        return "JsonMedicalMedicine{" +
                "name='" + name + '\'' +
                ", strength='" + strength + '\'' +
                ", dailyFrequency='" + dailyFrequency + '\'' +
                ", course='" + course + '\'' +
                ", medicationWithFood='" + medicationWithFood + '\'' +
                ", pharmacyCategory='" + pharmacyCategory + '\'' +
                ", isFavourite=" + isFavourite +
                '}';
    }
}