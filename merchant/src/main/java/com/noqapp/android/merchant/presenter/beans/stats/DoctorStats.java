package com.noqapp.android.merchant.presenter.beans.stats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

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
public class DoctorStats implements Serializable {

    @JsonProperty("yearly")
    private List<YearlyData> twelveMonths;

    @JsonProperty("rc")
    private NewRepeatCustomers repeatCustomers;

    public List<YearlyData> getTwelveMonths() {
        return twelveMonths;
    }

    public DoctorStats setTwelveMonths(List<YearlyData> twelveMonths) {
        this.twelveMonths = twelveMonths;
        return this;
    }

    public NewRepeatCustomers getRepeatCustomers() {
        return repeatCustomers;
    }

    public DoctorStats setRepeatCustomers(NewRepeatCustomers repeatCustomers) {
        this.repeatCustomers = repeatCustomers;
        return this;
    }

    @Override
    public String toString() {
        return "DoctorStats{" +
                "twelveMonths=" + twelveMonths +
                ", repeatCustomers=" + repeatCustomers +
                '}';
    }
}
