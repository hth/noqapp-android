package com.noqapp.android.common.beans.marketplace;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.body.GeoPointOfQ;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * hitender
 * 2/27/21 8:07 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketplaceElastic extends AbstractDomain {

    @JsonProperty("id")
    private String id;

    @JsonProperty("BT")
    private BusinessTypeEnum businessType;

    @JsonProperty("PP")
    private String productPrice;

    @JsonProperty("TI")
    private String title;

    @JsonProperty("DS")
    private String description;

    @JsonProperty("PI")
    private Set<String> postImages = new LinkedHashSet<>();

    /** Tags are going to be category under business type. Like Rent has category of Apartment, House. */
    @JsonProperty("TG")
    private String tag;

    @JsonProperty("LC")
    private int likeCount;

    @JsonProperty("EC")
    private int expressedInterestCount;

    @JsonProperty("COR")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty("GH")
    private String geoHash;

    @JsonProperty("MC")
    private String city;

    @JsonProperty("TO")
    private String town;

    @JsonProperty("CS")
    private String countryShortName;

    /** Mostly used for display as most of the common data is listed as text here. */
    @JsonProperty("TS")
    private String[] fieldTags;

    public String getId() {
        return id;
    }

    public MarketplaceElastic setId(String id) {
        this.id = id;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public MarketplaceElastic setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public MarketplaceElastic setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MarketplaceElastic setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MarketplaceElastic setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<String> getPostImages() {
        return postImages;
    }

    public MarketplaceElastic setPostImages(Set<String> postImages) {
        this.postImages = postImages;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public MarketplaceElastic setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public MarketplaceElastic setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public int getExpressedInterestCount() {
        return expressedInterestCount;
    }

    public MarketplaceElastic setExpressedInterestCount(int expressedInterestCount) {
        this.expressedInterestCount = expressedInterestCount;
        return this;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public MarketplaceElastic setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public MarketplaceElastic setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getCity() {
        return city;
    }

    public MarketplaceElastic setCity(String city) {
        this.city = city;
        return this;
    }

    public String getTown() {
        return town;
    }

    public MarketplaceElastic setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public MarketplaceElastic setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String[] getFieldTags() {
        return fieldTags;
    }

    public MarketplaceElastic setFieldTags(String[] fieldTags) {
        this.fieldTags = fieldTags;
        return this;
    }
}
