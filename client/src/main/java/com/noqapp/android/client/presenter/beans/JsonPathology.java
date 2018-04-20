package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class JsonPathology extends AbstractDomain {

    @JsonProperty("na")
    private String name;

    @JsonProperty("tr")
    private String testResult;

    public String getName() {
        return name;
    }

    public JsonPathology setName(String name) {
        this.name = name;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public JsonPathology setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}

