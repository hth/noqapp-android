package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 8/12/18 1:52 PM
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
public class JsonPreferredBusiness implements Serializable {

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("bc")
    private String bizCategoryId;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("p")
    private String storePhone;

    @JsonProperty("a")
    private boolean active;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonPreferredBusiness setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonPreferredBusiness setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public JsonPreferredBusiness setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonPreferredBusiness setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonPreferredBusiness setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonPreferredBusiness setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonPreferredBusiness setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonPreferredBusiness setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonPreferredBusiness setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonPreferredBusiness setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonPreferredBusiness setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public JsonPreferredBusiness setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public JsonPreferredBusiness setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public String toString() {
        return "JsonPreferredBusiness{" +
                "bizStoreId='" + bizStoreId + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", geoHash='" + geoHash + '\'' +
                ", businessName='" + businessName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", businessType=" + businessType +
                ", storeAddress='" + storeAddress + '\'' +
                ", area='" + area + '\'' +
                ", town='" + town + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", storePhone='" + storePhone + '\'' +
                '}';
    }
}
