package com.noqapp.android.common.utils;

public class GeoIP {
    private String ipAddress;
    private String cityName;
    private double latitude;
    private double longitude;

    public GeoIP() {
    }

    public GeoIP(String ipAddress, String cityName, double latitude, double longitude) {
        this.ipAddress = ipAddress;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoIP(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getIpAddress() {
        return ipAddress;
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
                "ipAddress='" + ipAddress + '\'' +
                ", cityName='" + cityName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
