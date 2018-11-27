package com.noqapp.android.merchant.model;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.merchant.views.pojos.DataObj;

import java.util.ArrayList;
import java.util.List;

public class MedicalCasePojo {

    private String name;
    private String address;
    private String details;
    private String knownAllergies;
    private String pastHistory;
    private String familyHistory;
    private String pulse;
    private String bloodPressure;
    private String weight;
    private String temperature;
    private String oxygenLevel;
    private String symptoms;
    private String clinicalFindings;
    private String examinationResults;
    private String diagnosis;
    private String provisionalDiagnosis;
    private String instructions;
    private String followup;

    private ArrayList<String> radiologyList = new ArrayList<>();
    private ArrayList<String> pathologyList = new ArrayList<>();

    private List<JsonMedicalMedicine> jsonMedicineList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public MedicalCasePojo setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public MedicalCasePojo setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public MedicalCasePojo setDetails(String details) {
        this.details = details;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public MedicalCasePojo setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public MedicalCasePojo setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public MedicalCasePojo setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getPulse() {
        return pulse;
    }

    public MedicalCasePojo setPulse(String pulse) {
        this.pulse = pulse;
        return this;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public MedicalCasePojo setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public MedicalCasePojo setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getTemperature() {
        return temperature;
    }

    public MedicalCasePojo setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getOxygenLevel() {
        return oxygenLevel;
    }

    public MedicalCasePojo setOxygenLevel(String oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
        return this;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public MedicalCasePojo setSymptoms(String symptoms) {
        this.symptoms = symptoms;
        return this;
    }

    public String getClinicalFindings() {
        return clinicalFindings;
    }

    public MedicalCasePojo setClinicalFindings(String clinicalFindings) {
        this.clinicalFindings = clinicalFindings;
        return this;
    }

    public String getExaminationResults() {
        return examinationResults;
    }

    public MedicalCasePojo setExaminationResults(String examinationResults) {
        this.examinationResults = examinationResults;
        return this;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public MedicalCasePojo setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public String getProvisionalDiagnosis() {
        return provisionalDiagnosis;
    }

    public MedicalCasePojo setProvisionalDiagnosis(String provisionalDiagnosis) {
        this.provisionalDiagnosis = provisionalDiagnosis;
        return this;
    }

    public String getInstructions() {
        return instructions;
    }

    public MedicalCasePojo setInstructions(String instructions) {
        this.instructions = instructions;
        return this;
    }

    public String getFollowup() {
        return followup;
    }

    public MedicalCasePojo setFollowup(String followup) {
        this.followup = followup;
        return this;
    }

    public ArrayList<String> getRadiologyList() {
        return radiologyList;
    }

    public MedicalCasePojo setRadiologyList(ArrayList<String> radiologyList) {
        this.radiologyList = radiologyList;
        return this;
    }

    public ArrayList<String> getPathologyList() {
        return pathologyList;
    }

    public MedicalCasePojo setPathologyList(ArrayList<String> pathologyList) {
        this.pathologyList = pathologyList;
        return this;
    }

    public List<JsonMedicalMedicine> getJsonMedicineList() {
        return jsonMedicineList;
    }

    public MedicalCasePojo setJsonMedicineList(List<JsonMedicalMedicine> jsonMedicineList) {
        this.jsonMedicineList = jsonMedicineList;
        return this;
    }

    @Override
    public String toString() {
        return "MedicalCasePojo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", details='" + details + '\'' +
                ", knownAllergies='" + knownAllergies + '\'' +
                ", pastHistory='" + pastHistory + '\'' +
                ", familyHistory='" + familyHistory + '\'' +
                ", pulse='" + pulse + '\'' +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", weight='" + weight + '\'' +
                ", temperature='" + temperature + '\'' +
                ", oxygenLevel='" + oxygenLevel + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", clinicalFindings='" + clinicalFindings + '\'' +
                ", examinationResults='" + examinationResults + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", provisionalDiagnosis='" + provisionalDiagnosis + '\'' +
                ", instructions='" + instructions + '\'' +
                ", followup='" + followup + '\'' +
                ", radiologyList=" + radiologyList +
                ", pathologyList=" + pathologyList +
                ", jsonMedicineList=" + jsonMedicineList +
                '}';
    }
}
