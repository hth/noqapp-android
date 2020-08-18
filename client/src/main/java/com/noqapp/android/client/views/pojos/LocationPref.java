package com.noqapp.android.client.views.pojos;

import com.noqapp.android.client.utils.Constants;

import java.io.Serializable;

public class LocationPref implements Serializable {
    private double latitude = Constants.DEFAULT_LATITUDE;
    private double longitude = Constants.DEFAULT_LONGITUDE;
    private String city = Constants.DEFAULT_CITY;
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

    public String getCity() {
        return city;
    }

    public LocationPref setCity(String city) {
        this.city = city;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public LocationPref setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    @Override
    public String toString() {
        return "LocationPref{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", city='" + city + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
