package com.noqapp.android.common.beans.medical;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

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
public class JsonMedicalRadiology extends AbstractDomain implements Serializable {

    @JsonProperty("na")
    private String name;

    @JsonProperty("tr")
    private String testResult;

    public String getName() {
        return name;
    }

    public JsonMedicalRadiology setName(String name) {
        this.name = name;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public JsonMedicalRadiology setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }

    @Override
    public String toString() {
        return "JsonMedicalRadiology{" +
                "name='" + name + '\'' +
                ", testResult='" + testResult + '\'' +
                '}';
    }
}
