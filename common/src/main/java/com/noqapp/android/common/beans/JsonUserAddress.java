package com.noqapp.android.common.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * hitender
 * 5/16/18 10:07 AM
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
public class JsonUserAddress extends AbstractDomain implements Parcelable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("cn")
    private String customerName;

    @JsonProperty("ad")
    private String address;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("dt")
    private String district;

    @JsonProperty("st")
    private String state;

    @JsonProperty("ss")
    private String stateShortName;

    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lng")
    private String longitude;

    @JsonProperty("pa")
    private boolean primaryAddress;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public JsonUserAddress(){}

    protected JsonUserAddress(Parcel in) {
        id = in.readString();
        customerName = in.readString();
        address = in.readString();
        geoHash = in.readString();
        countryShortName = in.readString();
        area = in.readString();
        town = in.readString();
        district = in.readString();
        state = in.readString();
        stateShortName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        primaryAddress = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(customerName);
        dest.writeString(address);
        dest.writeString(geoHash);
        dest.writeString(countryShortName);
        dest.writeString(area);
        dest.writeString(town);
        dest.writeString(district);
        dest.writeString(state);
        dest.writeString(stateShortName);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeByte((byte) (primaryAddress ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JsonUserAddress> CREATOR = new Creator<JsonUserAddress>() {
        @Override
        public JsonUserAddress createFromParcel(Parcel in) {
            return new JsonUserAddress(in);
        }

        @Override
        public JsonUserAddress[] newArray(int size) {
            return new JsonUserAddress[size];
        }
    };

    public String getId() {
        return id;
    }

    public JsonUserAddress setId(String id) {
        this.id = id;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonUserAddress setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JsonUserAddress setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public JsonUserAddress setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonUserAddress setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonUserAddress setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonUserAddress setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public JsonUserAddress setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public JsonUserAddress setState(String state) {
        this.state = state;
        return this;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public JsonUserAddress setStateShortName(String stateShortName) {
        this.stateShortName = stateShortName;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public JsonUserAddress setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public JsonUserAddress setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public boolean isPrimaryAddress() {
        return primaryAddress;
    }

    public JsonUserAddress setPrimaryAddress(boolean primaryAddress) {
        this.primaryAddress = primaryAddress;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonUserAddress setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @JsonIgnore
    public String getLocationAsString() {
        if (StringUtils.isNotBlank(area) && StringUtils.isNotBlank(town)) {
            return area + ", " + town;
        }
        return StringUtils.isNotBlank(town) ? town : "";
    }

    @Override
    public String toString() {
        return "JsonUserAddress{" +
            "id='" + id + '\'' +
            ", customerName='" + customerName + '\'' +
            ", address='" + address + '\'' +
            ", geoHash='" + geoHash + '\'' +
            ", countryShortName='" + countryShortName + '\'' +
            ", area='" + area + '\'' +
            ", town='" + town + '\'' +
            ", district='" + district + '\'' +
            ", state='" + state + '\'' +
            ", stateShortName='" + stateShortName + '\'' +
            ", latitude='" + latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            ", primaryAddress=" + primaryAddress +
            '}';
    }
}
