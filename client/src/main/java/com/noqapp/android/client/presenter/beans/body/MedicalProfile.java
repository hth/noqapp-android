package com.noqapp.android.client.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.JsonUserMedicalProfile;
import com.noqapp.android.common.model.types.medical.HospitalVisitForEnum;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 2019-05-08 12:43
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
public class MedicalProfile implements Serializable {

    @JsonProperty("qid")
    private String medicalProfileOfQueueUserId;

    @JsonProperty("gqd")
    private String guardianQueueUserId;

    @JsonProperty ("mp")
    private JsonUserMedicalProfile jsonUserMedicalProfile;

    public String getMedicalProfileOfQueueUserId() {
        return medicalProfileOfQueueUserId;
    }

    public MedicalProfile setMedicalProfileOfQueueUserId(String medicalProfileOfQueueUserId) {
        this.medicalProfileOfQueueUserId = medicalProfileOfQueueUserId;
        return this;
    }

    public String getGuardianQueueUserId() {
        return guardianQueueUserId;
    }

    public MedicalProfile setGuardianQueueUserId(String guardianQueueUserId) {
        this.guardianQueueUserId = guardianQueueUserId;
        return this;
    }

    public JsonUserMedicalProfile getJsonUserMedicalProfile() {
        return jsonUserMedicalProfile;
    }

    public MedicalProfile setJsonUserMedicalProfile(JsonUserMedicalProfile jsonUserMedicalProfile) {
        this.jsonUserMedicalProfile = jsonUserMedicalProfile;
        return this;
    }
}