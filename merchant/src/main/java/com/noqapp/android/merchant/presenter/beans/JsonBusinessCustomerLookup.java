package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * hitender
 * 6/17/18 3:46 PM
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonBusinessCustomerLookup implements Serializable {

    @JsonProperty("p")
    private String customerPhone;

    @JsonProperty("bc")
    private String businessCustomerId;

    @JsonProperty("qr")
    private String codeQR;

    public String getCustomerPhone() {
        return customerPhone;
    }

    public JsonBusinessCustomerLookup setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public JsonBusinessCustomerLookup setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonBusinessCustomerLookup setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }
}
