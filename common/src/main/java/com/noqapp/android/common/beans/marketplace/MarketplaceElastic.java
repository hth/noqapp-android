package com.noqapp.android.common.beans.marketplace;

import android.os.Build;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.body.GeoPointOfQ;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
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
public class MarketplaceElastic extends AbstractDomain implements Serializable {

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

    @JsonProperty("VC")
    private int viewCount;

    @JsonProperty("RA")
    private String rating;

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

    @JsonProperty("error")
    private ErrorEncounteredJson error;

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

    public int getViewCount() {
        return viewCount;
    }

    public MarketplaceElastic setViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public String getRating() {
        return rating;
    }

    public MarketplaceElastic setRating(String rating) {
        this.rating = rating;
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

    public ErrorEncounteredJson getError() {
        return error;
    }

    public MarketplaceElastic setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    public String townCity() {
        if (StringUtils.isBlank(town) && StringUtils.isBlank(city)) {
            return "";
        } else if (StringUtils.isBlank(town)) {
            return city;
        } else if (StringUtils.isBlank(city)) {
            return town;
        } else {
            return town + ", " + city;
        }
    }

    public String getValueFromTag(String field) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String found = Arrays.stream(tag.split(" ")).filter(x -> x.endsWith(field.toUpperCase())).findFirst().orElse(null);
            return found != null ? found.replaceAll("_" + field, "") : "";
        } else {
            String[] tags = tag.split(" ");
            for (String individualTag : tags) {
                if (individualTag.endsWith(field.toUpperCase())) {
                    return individualTag.replaceAll("_" + field, "");
                }
            }
            return "";
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MarketplaceElastic{");
        sb.append("id='").append(id).append('\'');
        sb.append(", businessType=").append(businessType);
        sb.append(", productPrice='").append(productPrice).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", postImages=").append(postImages);
        sb.append(", tag='").append(tag).append('\'');
        sb.append(", viewCount=").append(viewCount);
        sb.append(", rating='").append(rating).append('\'');
        sb.append(", geoPointOfQ=").append(geoPointOfQ);
        sb.append(", geoHash='").append(geoHash).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", town='").append(town).append('\'');
        sb.append(", countryShortName='").append(countryShortName).append('\'');
        sb.append(", fieldTags=").append(Arrays.toString(fieldTags));
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
