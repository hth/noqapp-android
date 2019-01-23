package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.ErrorEncounteredJson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 8/12/18 11:40 PM
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
public class JsonPreferredBusinessList implements Serializable {
    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("pbs")
    private List<JsonPreferredBusiness> preferredBusinesses = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCodeQR() {
        return codeQR;
    }

    public JsonPreferredBusinessList setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public List<JsonPreferredBusiness> getPreferredBusinesses() {
        return preferredBusinesses;
    }

    public JsonPreferredBusinessList setPreferredBusinesses(List<JsonPreferredBusiness> preferredBusinesses) {
        this.preferredBusinesses = preferredBusinesses;
        return this;
    }

    public JsonPreferredBusinessList addPreferredBusinesses(List<JsonPreferredBusiness> preferredBusinesses) {
        this.preferredBusinesses.addAll(preferredBusinesses);
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
        final StringBuffer sb = new StringBuffer("JsonPreferredBusinessList{");
        sb.append("preferredBusinesses=").append(preferredBusinesses);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
