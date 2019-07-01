package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-01 12:33
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
public class JsonProfessionalProfileTVList extends AbstractDomain {

    @JsonProperty("pps")
    private List<JsonProfessionalProfileTV> jsonProfessionalProfileTV = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }


    public List<JsonProfessionalProfileTV> getJsonProfessionalProfileTV() {
        return jsonProfessionalProfileTV;
    }

    public JsonProfessionalProfileTVList setJsonProfessionalProfileTV(List<JsonProfessionalProfileTV> jsonProfessionalProfileTV) {
        this.jsonProfessionalProfileTV = jsonProfessionalProfileTV;
        return this;
    }

    public JsonProfessionalProfileTVList addJsonProfessionalProfileTV(JsonProfessionalProfileTV jsonProfessionalProfileTV) {
        this.jsonProfessionalProfileTV.add(jsonProfessionalProfileTV);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonProfessionalProfileTVList{");
        sb.append("jsonProfessionalProfileTV=").append(jsonProfessionalProfileTV);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}