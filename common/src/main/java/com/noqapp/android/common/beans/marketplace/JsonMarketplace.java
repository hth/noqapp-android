package com.noqapp.android.common.beans.marketplace;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * hitender
 * 3/7/21 12:09 PM
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
public abstract class JsonMarketplace extends AbstractDomain implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("cor")
    private double[] coordinate;

    @JsonProperty("pp")
    private int productPrice;

    @JsonProperty("ti")
    private String title;

    @JsonProperty("ds")
    private String description;

    @JsonProperty("pi")
    private Set<String> postImages = new LinkedHashSet<>();

    @JsonProperty("tg")
    private String tags;

    @JsonProperty("vc")
    private int viewCount;

    /** Marketplace location. */
    @JsonProperty("ma")
    private String address;

    @JsonProperty("mc")
    private String city;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("lm")
    private String landmark;

    @JsonProperty("pu")
    private Date publishUntil;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getId() {
        return id;
    }

    public JsonMarketplace setId(String id) {
        this.id = id;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonMarketplace setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonMarketplace setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public JsonMarketplace setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public JsonMarketplace setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonMarketplace setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JsonMarketplace setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<String> getPostImages() {
        return postImages;
    }

    public JsonMarketplace setPostImages(Set<String> postImages) {
        this.postImages = postImages;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public JsonMarketplace setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public int getViewCount() {
        return viewCount;
    }

    public JsonMarketplace setViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JsonMarketplace setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public JsonMarketplace setCity(String city) {
        this.city = city;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonMarketplace setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonMarketplace setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getLandmark() {
        return landmark;
    }

    public JsonMarketplace setLandmark(String landmark) {
        this.landmark = landmark;
        return this;
    }

    public Date getPublishUntil() {
        return publishUntil;
    }

    public JsonMarketplace setPublishUntil(Date publishUntil) {
        this.publishUntil = publishUntil;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonMarketplace setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonMarketplace{" +
            "id='" + id + '\'' +
            ", queueUserId='" + queueUserId + '\'' +
            ", businessType=" + businessType +
            ", coordinate=" + Arrays.toString(coordinate) +
            ", productPrice=" + productPrice +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", postImages=" + postImages +
            ", tags='" + tags + '\'' +
            ", viewCount=" + viewCount +
            ", address='" + address + '\'' +
            ", city='" + city + '\'' +
            ", town='" + town + '\'' +
            ", countryShortName='" + countryShortName + '\'' +
            ", landmark='" + landmark + '\'' +
            ", publishUntil=" + publishUntil +
            ", error=" + error +
            '}';
    }
}
