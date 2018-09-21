package com.noqapp.android.client.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 5/16/18 10:12 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonUserAddressList extends AbstractDomain {

    @JsonProperty("ads")
    private List<JsonUserAddress> jsonUserAddresses = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonUserAddress> getJsonUserAddresses() {
        return jsonUserAddresses;
    }

    public JsonUserAddressList setJsonUserAddresses(List<JsonUserAddress> jsonUserAddresses) {
        this.jsonUserAddresses = jsonUserAddresses;
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
        final StringBuffer sb = new StringBuffer("JsonUserAddressList{");
        sb.append("jsonUserAddresses=").append(jsonUserAddresses);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}