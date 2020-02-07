package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.client.model.types.AccreditationEnum;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.model.types.FacilityEnum;
import com.noqapp.android.common.beans.body.GeoPointOfQ;
import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.model.types.AppointmentStateEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.WalkInStateEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 3/22/18.
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
public class BizStoreElastic implements Serializable {

    @JsonIgnore
    private String id;

    @JsonProperty("N")
    private String businessName;

    @JsonProperty("BT")
    private BusinessTypeEnum businessType;

    @JsonProperty("BC")
    private String bizCategoryName;

    @JsonProperty("BDI")
    private String bizCategoryDisplayImage;

    @JsonProperty("BCI")
    private String bizCategoryId;

    @JsonProperty("AD")
    private String address;

    @JsonProperty("AR")
    private String area;

    @JsonProperty("TO")
    private String town;

    @JsonProperty("DT")
    private String district;

    @JsonProperty("ST")
    private String state;

    @JsonProperty("SS")
    private String stateShortName;

    /* Postal code could be empty for few countries. */
    @JsonProperty("PC")
    private String postalCode;

    @JsonProperty("CC")
    private String country;

    @JsonProperty("CS")
    private String countryShortName;

    /* Phone number saved with country code. */
    @JsonProperty("PH")
    private String phone;

    /* To not loose user entered phone number. */
    @JsonProperty("PR")
    private String phoneRaw;

    @JsonProperty("COR")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty("PI")
    private String placeId;

    @JsonProperty("PT")
    private String[] placeType;

    @JsonProperty("RA")
    private float rating;

    @JsonProperty("RC")
    private int reviewCount;

    @JsonProperty("BID")
    private String bizNameId;

    @JsonProperty("DN")
    private String displayName;

    @JsonProperty("QR")
    private String codeQR;

    @JsonProperty("TZ")
    private String timeZone;

    @JsonProperty("GH")
    private String geoHash;

    @JsonProperty("WL")
    private String webLocation;

    @JsonProperty("FF")
    private String famousFor;

    @JsonProperty("DI")
    private String displayImage;

    @JsonProperty("SH")
    private List<StoreHourElastic> storeHourElasticList = new ArrayList<>();

    @JsonProperty("PP")
    private int productPrice;

    @JsonProperty("WS")
    private WalkInStateEnum walkInState;

    @JsonProperty("PS")
    private AppointmentStateEnum appointmentState;

    @JsonProperty("PD")
    private int appointmentDuration;

    @JsonProperty("PF")
    private int appointmentOpenHowFar;

    @JsonProperty("BI")
    private List<String> bizServiceImages = new LinkedList<>();

    @JsonProperty("AM")
    private List<AmenityEnum> amenities = new LinkedList<>();

    @JsonProperty("FA")
    private List<FacilityEnum> facilities = new LinkedList<>();

    @JsonProperty("AC")
    private List<AccreditationEnum> accreditation = new LinkedList<>();

    /** WP is populated when the BT is of type BusinessTypeEnum.DO. */
    @JsonProperty("WP")
    private String webProfileId;

    /** ED is populated when the BT is of type BusinessTypeEnum.DO. */
    @JsonProperty("ED")
    private List<JsonNameDatePair> education;

    @JsonProperty("A")
    private boolean active = true;

    public String getId() {
        return id;
    }

    public BizStoreElastic setId(String id) {
        this.id = id;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BizStoreElastic setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public BizStoreElastic setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBizCategoryName() {
        return bizCategoryName;
    }

    public BizStoreElastic setBizCategoryName(String bizCategoryName) {
        this.bizCategoryName = bizCategoryName;
        return this;
    }

    public String getBizCategoryDisplayImage() {
        return bizCategoryDisplayImage;
    }

    public BizStoreElastic setBizCategoryDisplayImage(String bizCategoryDisplayImage) {
        this.bizCategoryDisplayImage = bizCategoryDisplayImage;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public BizStoreElastic setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
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

    public int getReviewCount() {
        return reviewCount;
    }

    public BizStoreElastic setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
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

    public String getFamousFor() {
        return famousFor;
    }

    public BizStoreElastic setFamousFor(String famousFor) {
        this.famousFor = famousFor;
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

    public int getProductPrice() {
        return productPrice;
    }

    public BizStoreElastic setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public WalkInStateEnum getWalkInState() {
        return walkInState;
    }

    public void setWalkInState(WalkInStateEnum walkInState) {
        this.walkInState = walkInState;
    }

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public BizStoreElastic setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public BizStoreElastic setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
        return this;
    }

    public int getAppointmentOpenHowFar() {
        return appointmentOpenHowFar;
    }

    public BizStoreElastic setAppointmentOpenHowFar(int appointmentOpenHowFar) {
        this.appointmentOpenHowFar = appointmentOpenHowFar;
        return this;
    }

    public List<String> getBizServiceImages() {
        return bizServiceImages;
    }

    public BizStoreElastic setBizServiceImages(List<String> bizServiceImages) {
        this.bizServiceImages = bizServiceImages;
        return this;
    }

    public List<AmenityEnum> getAmenities() {
        return amenities;
    }

    public BizStoreElastic setAmenities(List<AmenityEnum> amenities) {
        this.amenities = amenities;
        return this;
    }

    public List<FacilityEnum> getFacilities() {
        return facilities;
    }

    public BizStoreElastic setFacilities(List<FacilityEnum> facilities) {
        this.facilities = facilities;
        return this;
    }

    public List<AccreditationEnum> getAccreditation() {
        return accreditation;
    }

    public BizStoreElastic setAccreditation(List<AccreditationEnum> accreditation) {
        this.accreditation = accreditation;
        return this;
    }

    public String getWebProfileId() {
        return webProfileId;
    }

    public BizStoreElastic setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public BizStoreElastic setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public String getCompleteEducation() {
        if (null == education || education.size() == 0)
            return "";
        else {
            StringBuilder edu = new StringBuilder();
            for (int i = 0; i < education.size(); i++) {
                edu.append(education.get(i).getName()).append(", ");
            }
            if (edu.toString().endsWith(", ")) {
                edu = new StringBuilder(edu.substring(0, edu.length() - 2));
            }
            return edu.toString();
        }
    }

    public boolean isActive() {
        return active;
    }

    public BizStoreElastic setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public String toString() {
        return "BizStoreElastic{" +
                "id='" + id + '\'' +
                ", businessName='" + businessName + '\'' +
                ", businessType=" + businessType +
                ", bizCategoryName='" + bizCategoryName + '\'' +
                ", bizCategoryDisplayImage='" + bizCategoryDisplayImage + '\'' +
                ", bizCategoryId='" + bizCategoryId + '\'' +
                ", address='" + address + '\'' +
                ", area='" + area + '\'' +
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
                ", reviewCount=" + reviewCount +
                ", bizNameId='" + bizNameId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", geoHash='" + geoHash + '\'' +
                ", webLocation='" + webLocation + '\'' +
                ", famousFor='" + famousFor + '\'' +
                ", displayImage='" + displayImage + '\'' +
                ", storeHourElasticList=" + storeHourElasticList +
                ", bizServiceImages=" + bizServiceImages +
                ", amenities=" + amenities +
                ", facilities=" + facilities +
                ", accreditation=" + accreditation +
                ", webProfileId='" + webProfileId + '\'' +
                ", education=" + education +
                ", active=" + active +
                '}';
    }
}
