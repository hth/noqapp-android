package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.medical.MedicalRecordFieldFilterEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 9/13/19 10:40 AM
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
public class CodeQRDateRangeLookup extends AbstractDomain {

    @JsonProperty("codeQR")
    private String codeQR;

    @JsonProperty("populateField")
    private MedicalRecordFieldFilterEnum medicalRecordFieldFilter;

    @JsonProperty("from")
    private String from;

    @JsonProperty("until")
    private String until;

    @JsonProperty("cp")
    private int currentPosition;

    public String getCodeQR() {
        return codeQR;
    }

    public CodeQRDateRangeLookup setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public MedicalRecordFieldFilterEnum getMedicalRecordFieldFilter() {
        return medicalRecordFieldFilter;
    }

    public CodeQRDateRangeLookup setMedicalRecordFieldFilter(MedicalRecordFieldFilterEnum medicalRecordFieldFilter) {
        this.medicalRecordFieldFilter = medicalRecordFieldFilter;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public CodeQRDateRangeLookup setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getUntil() {
        return until;
    }

    public CodeQRDateRangeLookup setUntil(String until) {
        this.until = until;
        return this;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public CodeQRDateRangeLookup setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        return this;
    }
}