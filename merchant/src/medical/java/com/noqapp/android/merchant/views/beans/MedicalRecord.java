package com.noqapp.android.merchant.views.beans;

import com.noqapp.common.beans.medical.JsonMedicine;

public class MedicalRecord {

    private String medicName;
    private int medication_type =0;
    private int dose_size = 0;
    private int frequency = 0;
    private int course = 0;
    private int before_after_food = 0;
    private JsonMedicine jsonMedicine = new JsonMedicine();


    public String getMedicName() {
        return medicName;
    }

    public MedicalRecord setMedicName(String medicName) {
        this.medicName = medicName;
        return this;
    }

    public int getMedication_type() {
        return medication_type;
    }

    public MedicalRecord setMedication_type(int medication_type) {
        this.medication_type = medication_type;
        return this;
    }

    public int getDose_size() {
        return dose_size;
    }

    public MedicalRecord setDose_size(int dose_size) {
        this.dose_size = dose_size;
        return this;
    }

    public int getFrequency() {
        return frequency;
    }

    public MedicalRecord setFrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    public int getCourse() {
        return course;
    }

    public MedicalRecord setCourse(int course) {
        this.course = course;
        return this;
    }

    public int getBefore_after_food() {
        return before_after_food;
    }

    public MedicalRecord setBefore_after_food(int before_after_food) {
        this.before_after_food = before_after_food;
        return this;
    }

    public JsonMedicine getJsonMedicine() {
        return jsonMedicine;
    }

    public MedicalRecord setJsonMedicine(JsonMedicine jsonMedicine) {
        this.jsonMedicine = jsonMedicine;
        return this;
    }
}
