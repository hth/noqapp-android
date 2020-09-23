package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Merchant this object is different from client object. It fills different requirement.
 * Decided not to merge for now.
 * User: hitender
 * Date: 4/16/17 5:49 PM
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonToken implements Serializable {

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("bc")
    private String bizCategoryId;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("ds")
    private String displayServingNumber;

    @JsonProperty("t")
    private int token;

    @JsonProperty("n")
    private String customerName;

    @JsonProperty("e")
    private String expectedServiceBegin;

    /* Do not use it. Its not being used or sent across the line. */
    @JsonProperty("ti")
    @JsonIgnore
    @SuppressWarnings("unused")
    private String transactionId;

    @JsonProperty("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonToken setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonToken setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public void setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
    }

    public String getDisplayServingNumber() {
        return displayServingNumber;
    }

    public JsonToken setDisplayServingNumber(String displayServingNumber) {
        this.displayServingNumber = displayServingNumber;
        return this;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getExpectedServiceBegin() {
        return expectedServiceBegin;
    }

    public JsonToken setExpectedServiceBegin(String expectedServiceBegin) {
        this.expectedServiceBegin = expectedServiceBegin;
        return this;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public JsonToken setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @JsonIgnoreProperties
    public int getRemaining() {
        return token - servingNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("codeQR", codeQR)
                .append("displayName", displayName)
                .append("queueStatus", queueStatus)
                .append("servingNumber", servingNumber)
                .append("token", token)
                .append("customerName", customerName)
                .append("expectedServiceBegin", expectedServiceBegin)
                .append("error", error)
                .toString();
    }
}