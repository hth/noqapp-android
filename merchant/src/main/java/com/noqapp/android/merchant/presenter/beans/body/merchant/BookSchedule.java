package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 2019-06-03 17:00
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
public class BookSchedule  implements Serializable {

    @JsonProperty("sc")
    private JsonSchedule jsonSchedule;

    @JsonProperty("bc")
    private JsonBusinessCustomer businessCustomer;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public JsonSchedule getJsonSchedule() {
        return jsonSchedule;
    }

    public BookSchedule setJsonSchedule(JsonSchedule jsonSchedule) {
        this.jsonSchedule = jsonSchedule;
        return this;
    }

    public JsonBusinessCustomer getBusinessCustomer() {
        return businessCustomer;
    }

    public BookSchedule setBusinessCustomer(JsonBusinessCustomer businessCustomer) {
        this.businessCustomer = businessCustomer;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public BookSchedule setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }
}