package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.client.model.types.BusinessTypeEnum;

import java.util.List;

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
public class JsonMedicalRecord extends AbstractDomain {

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("cc")
    private String chiefComplain;

    @JsonProperty("ph")
    private String pastHistory;

    @JsonProperty("fh")
    private String familyHistory;

    @JsonProperty("ka")
    private String knownAllergies;

    @JsonProperty("pe")
    private List<JsonMedicalPhysicalExamination> medicalPhysicalExaminations;

    @JsonProperty("cf")
    private String clinicalFinding;

    @JsonProperty("dd")
    private String provisionalDifferentialDiagnosis;

    @JsonProperty("le")
    private List<JsonPathology> pathologies;

    @JsonProperty("pr")
    private String pathologyTestResult;

    @JsonProperty("re")
    private List<JsonMedicalRadiologyXRay> medicalRadiologyXRays;

    @JsonProperty("me")
    private List<JsonMedicine> medicines;

    @JsonProperty("dbi")
    private String diagnosedBy;

    @JsonProperty("ra")
    private List<JsonRecordAccess> recordAccess;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("bc")
    private String bizCategory;

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonMedicalRecord setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonMedicalRecord setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getChiefComplain() {
        return chiefComplain;
    }

    public JsonMedicalRecord setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public JsonMedicalRecord setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public JsonMedicalRecord setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public JsonMedicalRecord setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public List<JsonMedicalPhysicalExamination> getMedicalPhysicalExaminations() {
        return medicalPhysicalExaminations;
    }

    public JsonMedicalRecord setMedicalPhysicalExaminations(List<JsonMedicalPhysicalExamination> medicalPhysicalExaminations) {
        this.medicalPhysicalExaminations = medicalPhysicalExaminations;
        return this;
    }

    public String getClinicalFinding() {
        return clinicalFinding;
    }

    public JsonMedicalRecord setClinicalFinding(String clinicalFinding) {
        this.clinicalFinding = clinicalFinding;
        return this;
    }

    public String getProvisionalDifferentialDiagnosis() {
        return provisionalDifferentialDiagnosis;
    }

    public JsonMedicalRecord setProvisionalDifferentialDiagnosis(String provisionalDifferentialDiagnosis) {
        this.provisionalDifferentialDiagnosis = provisionalDifferentialDiagnosis;
        return this;
    }

    public List<JsonPathology> getPathologies() {
        return pathologies;
    }

    public JsonMedicalRecord setPathologies(List<JsonPathology> pathologies) {
        this.pathologies = pathologies;
        return this;
    }

    public String getPathologyTestResult() {
        return pathologyTestResult;
    }

    public JsonMedicalRecord setPathologyTestResult(String pathologyTestResult) {
        this.pathologyTestResult = pathologyTestResult;
        return this;
    }

    public List<JsonMedicalRadiologyXRay> getMedicalRadiologyXRays() {
        return medicalRadiologyXRays;
    }

    public JsonMedicalRecord setMedicalRadiologyXRays(List<JsonMedicalRadiologyXRay> medicalRadiologyXRays) {
        this.medicalRadiologyXRays = medicalRadiologyXRays;
        return this;
    }

    public List<JsonMedicine> getMedicines() {
        return medicines;
    }

    public JsonMedicalRecord setMedicines(List<JsonMedicine> medicines) {
        this.medicines = medicines;
        return this;
    }

    public String getDiagnosedBy() {
        return diagnosedBy;
    }

    public JsonMedicalRecord setDiagnosedBy(String diagnosedBy) {
        this.diagnosedBy = diagnosedBy;
        return this;
    }

    public List<JsonRecordAccess> getRecordAccess() {
        return recordAccess;
    }

    public JsonMedicalRecord setRecordAccess(List<JsonRecordAccess> recordAccess) {
        this.recordAccess = recordAccess;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonMedicalRecord setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBizCategory() {
        return bizCategory;
    }

    public JsonMedicalRecord setBizCategory(String bizCategory) {
        this.bizCategory = bizCategory;
        return this;
    }
}