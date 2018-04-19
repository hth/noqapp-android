package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

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
public class JsonRecordAccess extends AbstractDomain {

    @JsonProperty("raq")
    private String recordAccessedQid;

    @JsonProperty("rad")
    private Date recordAccessedDate;

    public String getRecordAccessedQid() {
        return recordAccessedQid;
    }

    public JsonRecordAccess setRecordAccessedQid(String recordAccessedQid) {
        this.recordAccessedQid = recordAccessedQid;
        return this;
    }

    public Date getRecordAccessedDate() {
        return recordAccessedDate;
    }

    public JsonRecordAccess setRecordAccessedDate(Date recordAccessedDate) {
        this.recordAccessedDate = recordAccessedDate;
        return this;
    }
}