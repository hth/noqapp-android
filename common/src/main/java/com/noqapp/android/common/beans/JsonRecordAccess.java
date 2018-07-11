package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class JsonRecordAccess extends AbstractDomain implements Serializable{

    @JsonProperty("raq")
    private String recordAccessedQid;

    @JsonProperty("rad")
    private String recordAccessedDate;

    public String getRecordAccessedQid() {
        return recordAccessedQid;
    }

    public JsonRecordAccess setRecordAccessedQid(String recordAccessedQid) {
        this.recordAccessedQid = recordAccessedQid;
        return this;
    }

    public String getRecordAccessedDate() {
        return recordAccessedDate;
    }

    public JsonRecordAccess setRecordAccessedDate(String recordAccessedDate) {
        this.recordAccessedDate = recordAccessedDate;
        return this;
    }
}