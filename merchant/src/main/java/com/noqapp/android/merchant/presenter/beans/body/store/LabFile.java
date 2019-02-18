package com.noqapp.android.merchant.presenter.beans.body.store;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.medical.LabCategoryEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2019-02-14 16:51
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
public class LabFile implements Serializable {

    @JsonProperty("ti")
    private String transactionId;

    @JsonProperty("da")
    private String deleteAttachment;

    @JsonProperty("files")
    private List<String> files = new LinkedList<>();

    @JsonProperty("rr")
    private String recordReferenceId;

    @JsonProperty("ob")
    private String observation;

    @JsonProperty("lc")
    private LabCategoryEnum labCategory;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getTransactionId() {
        return transactionId;
    }

    public LabFile setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getDeleteAttachment() {
        return deleteAttachment;
    }

    public LabFile setDeleteAttachment(String deleteAttachment) {
        this.deleteAttachment = deleteAttachment;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public LabFile setFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public LabFile setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }

    public String getObservation() {
        return observation;
    }

    public LabFile setObservation(String observation) {
        this.observation = observation;
        return this;
    }

    public LabCategoryEnum getLabCategory() {
        return labCategory;
    }

    public LabFile setLabCategory(LabCategoryEnum labCategory) {
        this.labCategory = labCategory;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }


    @Override
    public String toString() {
        return "LabFile{" +
                "transactionId='" + transactionId + '\'' +
                ", deleteAttachment='" + deleteAttachment + '\'' +
                ", files=" + files +
                ", recordReferenceId='" + recordReferenceId + '\'' +
                ", error=" + error +
                '}';
    }
}
