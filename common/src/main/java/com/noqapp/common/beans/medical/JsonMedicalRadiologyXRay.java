package com.noqapp.common.beans.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.beans.AbstractDomain;

import java.io.Serializable;

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
public class JsonMedicalRadiologyXRay extends AbstractDomain implements Serializable {

    @JsonProperty("na")
    private String name;

    @JsonProperty("tr")
    private String testResult;

    public String getName() {
        return name;
    }

    public JsonMedicalRadiologyXRay setName(String name) {
        this.name = name;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public JsonMedicalRadiologyXRay setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
