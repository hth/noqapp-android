package com.noqapp.android.client.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.client.presenter.beans.ErrorEncounteredJson;

/**
 * Created by chandra on 11/4/17.
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
public class AppVersionCheck {

    @JsonProperty("r")
    private int supportedVersion;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public int getSupportedVersion() {
        return supportedVersion;
    }

    public void setSupportedVersion(int supportedVersion) {
        this.supportedVersion = supportedVersion;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "AppVersionCheck{" +
                "supportedVersion=" + supportedVersion +
                ", error=" + error +
                '}';
    }
}

