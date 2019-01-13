package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseHistory {

    private String name;
    private String address;
    private String details;
    private String age;
    private String gender;
    private String pulse;
    private String [] bloodPressure;
    private String weight;
    private String temperature;
    private String oxygenLevel;
    private String height;
    private String respiratory;
    private String symptoms;
    private String clinicalFindings;
    private String examinationResults;
    private String diagnosis;
    private String provisionalDiagnosis;
    private String instructions;
    private String followup;

    private List<String> mriList = new ArrayList<>();
    private List<String> sonoList = new ArrayList<>();
    private List<String> scanList = new ArrayList<>();
    private List<String> xrayList = new ArrayList<>();
    private List<String> pathologyList = new ArrayList<>();

    private String pastHistory;
    private String familyHistory;
    private String knownAllergies;
    private String medicineAllergies;
    private boolean historyDirty;

    private List<JsonMedicalMedicine> jsonMedicineList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public CaseHistory setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CaseHistory setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public CaseHistory setDetails(String details) {
        this.details = details;
        return this;
    }

    public String getPulse() {
        return pulse;
    }

    public CaseHistory setPulse(String pulse) {
        this.pulse = pulse;
        return this;
    }

    public String[] getBloodPressure() {
        return bloodPressure;
    }

    public CaseHistory setBloodPressure(String[] bloodPressure) {
        this.bloodPressure = bloodPressure;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public CaseHistory setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getTemperature() {
        return temperature;
    }

    public CaseHistory setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getOxygenLevel() {
        return oxygenLevel;
    }

    public CaseHistory setOxygenLevel(String oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
        return this;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public CaseHistory setSymptoms(String symptoms) {
        this.symptoms = symptoms;
        return this;
    }

    public String getClinicalFindings() {
        return clinicalFindings;
    }

    public CaseHistory setClinicalFindings(String clinicalFindings) {
        this.clinicalFindings = clinicalFindings;
        return this;
    }

    public String getExaminationResults() {
        return examinationResults;
    }

    public CaseHistory setExaminationResults(String examinationResults) {
        this.examinationResults = examinationResults;
        return this;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public CaseHistory setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public String getProvisionalDiagnosis() {
        return provisionalDiagnosis;
    }

    public CaseHistory setProvisionalDiagnosis(String provisionalDiagnosis) {
        this.provisionalDiagnosis = provisionalDiagnosis;
        return this;
    }

    public String getInstructions() {
        return instructions;
    }

    public CaseHistory setInstructions(String instructions) {
        this.instructions = instructions;
        return this;
    }

    public String getFollowup() {
        return followup;
    }

    public CaseHistory setFollowup(String followup) {
        this.followup = followup;
        return this;
    }

    public List<String> getMriList() {
        return mriList;
    }

    public CaseHistory setMriList(ArrayList<String> mriList) {
        this.mriList = mriList;
        return this;
    }

    public List<String> getSonoList() {
        return sonoList;
    }

    public CaseHistory setSonoList(ArrayList<String> sonoList) {
        this.sonoList = sonoList;
        return this;
    }

    public List<String> getScanList() {
        return scanList;
    }

    public CaseHistory setScanList(ArrayList<String> scanList) {
        this.scanList = scanList;
        return this;
    }

    public List<String> getXrayList() {
        return xrayList;
    }

    public CaseHistory setXrayList(ArrayList<String> xrayList) {
        this.xrayList = xrayList;
        return this;
    }

    public List<String> getPathologyList() {
        return pathologyList;
    }

    public CaseHistory setPathologyList(ArrayList<String> pathologyList) {
        this.pathologyList = pathologyList;
        return this;
    }

    public List<JsonMedicalMedicine> getJsonMedicineList() {
        return jsonMedicineList;
    }

    public CaseHistory setJsonMedicineList(List<JsonMedicalMedicine> jsonMedicineList) {
        this.jsonMedicineList = jsonMedicineList;
        return this;
    }

    public String getAge() {
        return age;
    }

    public CaseHistory setAge(String age) {
        this.age = age;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public CaseHistory setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public CaseHistory setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getRespiratory() {
        return respiratory;
    }

    public CaseHistory setRespiratory(String respiratory) {
        this.respiratory = respiratory;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public CaseHistory setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public CaseHistory setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public CaseHistory setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public String getMedicineAllergies() {
        return medicineAllergies;
    }

    public CaseHistory setMedicineAllergies(String medicineAllergies) {
        this.medicineAllergies = medicineAllergies;
        return this;
    }

    public boolean isHistoryDirty() {
        return historyDirty;
    }

    public CaseHistory setHistoryDirty(boolean historyDirty) {
        this.historyDirty = historyDirty;
        return this;
    }

    @Override
    public String toString() {
        return "CaseHistory{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", details='" + details + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", knownAllergies='" + knownAllergies + '\'' +
                ", pastHistory='" + pastHistory + '\'' +
                ", familyHistory='" + familyHistory + '\'' +
                ", pulse='" + pulse + '\'' +
                ", bloodPressure=" + Arrays.toString(bloodPressure) +
                ", weight='" + weight + '\'' +
                ", temperature='" + temperature + '\'' +
                ", oxygenLevel='" + oxygenLevel + '\'' +
                ", height='" + height + '\'' +
                ", respiratory='" + respiratory + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", clinicalFindings='" + clinicalFindings + '\'' +
                ", examinationResults='" + examinationResults + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", provisionalDiagnosis='" + provisionalDiagnosis + '\'' +
                ", instructions='" + instructions + '\'' +
                ", followup='" + followup + '\'' +
                ", mriList=" + mriList +
                ", sonoList=" + sonoList +
                ", scanList=" + scanList +
                ", xrayList=" + xrayList +
                ", pathologyList=" + pathologyList +
                ", jsonMedicineList=" + jsonMedicineList +
                '}';
    }
}
