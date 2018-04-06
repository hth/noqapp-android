package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.client.model.types.BusinessTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hitender on 3/22/18.
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
public class BizStoreElastic implements Serializable {

    @JsonIgnore
    private String id;

    @JsonProperty("SI")
    private String scrollId;

    @JsonProperty("N")
    private String businessName;

    @JsonProperty("BT")
    private String businessType;

    @JsonProperty ("BC")
    private String category;

    @JsonProperty ("BCI")
    private String categoryId;

    @JsonProperty ("AD")
    private String address;

    @JsonProperty ("AR")
    private String area;

    @JsonProperty ("TO")
    private String town;

    @JsonProperty ("DT")
    private String district;

    @JsonProperty ("ST")
    private String state;

    @JsonProperty ("SS")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @JsonProperty ("PC")
    private String postalCode;

    @JsonProperty ("CC")
    private String country;

    @JsonProperty ("CS")
    private String countryShortName;

    /* Phone number saved with country code. */
    @JsonProperty ("PH")
    private String phone;

    /* To not loose user entered phone number. */
    @JsonProperty ("PR")
    private String phoneRaw;

    @JsonProperty ("COR")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty ("PI")
    private String placeId;

    @JsonProperty ("PT")
    private String[] placeType;

    @JsonProperty ("RA")
    private float rating;

    @JsonProperty ("RC")
    private int ratingCount;

    @JsonProperty ("BID")
    private String bizNameId;

    @JsonProperty("DN")
    private String displayName;

    @JsonProperty ("QR")
    private String codeQR;

    @JsonProperty ("TZ")
    private String timeZone;

    @JsonProperty ("GH")
    private String geoHash;

    @JsonProperty ("WL")
    private String webLocation;

    @JsonProperty ("DI")
    private String displayImage;

    @JsonProperty ("SH")
    private List<StoreHourElastic> storeHourElasticList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public BizStoreElastic setId(String id) {
        this.id = id;
        return this;
    }

    public String getScrollId() {
        return scrollId;
    }

    public BizStoreElastic setScrollId(String scrollId) {
        this.scrollId = scrollId;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BizStoreElastic setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBusinessType() {
        return businessType;
    }

    public BizStoreElastic setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType.getDescription();
        return this;
    }

    public String getCategory() {
        return category;
    }

    public BizStoreElastic setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public BizStoreElastic setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public BizStoreElastic setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getArea() {
        return area;
    }

    public BizStoreElastic setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public BizStoreElastic setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public BizStoreElastic setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public BizStoreElastic setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public BizStoreElastic setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public BizStoreElastic setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public BizStoreElastic setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public BizStoreElastic setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public BizStoreElastic setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public BizStoreElastic setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
        return this;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public BizStoreElastic setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public BizStoreElastic setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public String[] getPlaceType() {
        return placeType;
    }

    public BizStoreElastic setPlaceType(String[] placeType) {
        this.placeType = placeType;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public BizStoreElastic setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public BizStoreElastic setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public BizStoreElastic setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BizStoreElastic setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public BizStoreElastic setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public BizStoreElastic setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public BizStoreElastic setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getWebLocation() {
        return webLocation;
    }

    public BizStoreElastic setWebLocation(String webLocation) {
        this.webLocation = webLocation;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public BizStoreElastic setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
        return this;
    }

    public List<StoreHourElastic> getStoreHourElasticList() {
        return storeHourElasticList;
    }

    public BizStoreElastic setStoreHourElasticList(List<StoreHourElastic> storeHourElasticList) {
        this.storeHourElasticList = storeHourElasticList;
        return this;
    }

    @Override
    public String toString() {
        return "BizStoreElastic{" +
                "id='" + id + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                ", town='" + town + '\'' +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", stateShortName='" + stateShortName + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", phone='" + phone + '\'' +
                ", phoneRaw='" + phoneRaw + '\'' +
                ", geoPointOfQ=" + geoPointOfQ +
                ", placeId='" + placeId + '\'' +
                ", placeType=" + Arrays.toString(placeType) +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", bizNameId='" + bizNameId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}
