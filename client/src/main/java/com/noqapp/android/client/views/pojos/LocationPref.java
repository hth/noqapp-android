package com.noqapp.android.client.views.pojos;

import com.noqapp.android.client.utils.Constants;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class LocationPref implements Serializable {
    private double latitude = Constants.DEFAULT_LATITUDE;
    private double longitude = Constants.DEFAULT_LONGITUDE;
    private String area = "";
    private String town = Constants.DEFAULT_CITY;
    private String countryCode = Constants.DEFAULT_COUNTRY_CODE;

    public double getLatitude() {
        return latitude;
    }

    public LocationPref setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocationPref setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getArea() {
        return area;
    }

    public LocationPref setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public LocationPref setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public LocationPref setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getLocationAsString() {
        if (StringUtils.isNotBlank(area) && StringUtils.isNotBlank(town)) {
            return area + ", " + town;
        } else if (StringUtils.isNotBlank(area)) {
            return area;
        }
        return StringUtils.isNotBlank(town) ? town : "";
    }

    @Override
    public String toString() {
        return "LocationPref{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            ", area='" + area + '\'' +
            ", town='" + town + '\'' +
            ", countryCode='" + countryCode + '\'' +
            '}';
    }
}
