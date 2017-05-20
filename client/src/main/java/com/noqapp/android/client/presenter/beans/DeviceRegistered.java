package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: hitender
 * Date: 4/2/17 6:44 PM
 */

@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude (JsonInclude.Include.NON_NULL)
public class DeviceRegistered {

    @JsonProperty("r")
    private int registered;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("registered", registered)
                .toString();
    }
}

