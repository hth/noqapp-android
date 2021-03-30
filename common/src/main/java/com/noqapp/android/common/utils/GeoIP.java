package com.noqapp.android.common.utils;

public class GeoIP {
    private String area;
    private String town;
    private String cityName;
    private double latitude;
    private double longitude;

    public GeoIP() {
    }

    public GeoIP(String area, String town, String cityName, double latitude, double longitude) {
        this.area = area;
        this.town = town;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoIP(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getArea() {
        return area;
    }

    public String getTown() {
        return town;
    }

    public String getCityName() {
        return cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "GeoIP{" +
            "area='" + area + '\'' +
            ", town='" + town + '\'' +
            ", cityName='" + cityName + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
