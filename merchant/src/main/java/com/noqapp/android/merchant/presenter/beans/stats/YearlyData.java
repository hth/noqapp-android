package com.noqapp.android.merchant.presenter.beans.stats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * hitender
 * 5/15/18 8:58 AM
 */
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YearlyData implements Serializable {

    @JsonProperty("m")
    private int yearMonth;

    @JsonProperty("v")
    private int value;

    public int getYearMonth() {
        return yearMonth;
    }

    public YearlyData setYearMonth(int yearMonth) {
        this.yearMonth = yearMonth;
        return this;
    }

    public int getValue() {
        return value;
    }

    public YearlyData setValue(int value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "YearlyData{" +
                "yearMonth=" + yearMonth +
                ", value=" + value +
                '}';
    }
}
